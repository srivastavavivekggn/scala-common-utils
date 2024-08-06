package com.srivastavavivekggn.scala.reactive.crud

import com.srivastavavivekggn.scala.util.TypeAlias.JLong
import com.srivastavavivekggn.scala.util.domain.BaseModel
import com.srivastavavivekggn.scala.util.lang.StringUtils
import org.springframework.core.convert.converter.Converter
import org.springframework.data.domain.Sort.Order
import org.springframework.data.domain.{Page, PageImpl, PageRequest, Pageable, Sort}
import org.springframework.util.Assert
import reactor.core.publisher.{Flux, Mono}

import java.util.Objects

/**
  * Helper utilities that can be applied to any AbstractReactiveCrudOperationsImpl class.
  *
  * Provides implicit conversions between DAO and Entity class (and corresponding Flux, Mono, etc. versions)
  *
  * @tparam DAO the DAO (db specific) type
  * @tparam E   the domain entity type
  * @tparam ID  the entity ID type
  */
trait ReactiveCrudOperationsImplHelpers[DAO <: BaseModel[ID], E <: BaseModel[ID], ID <: java.io.Serializable] {
  this: AbstractReactiveCrudOperationsImpl[DAO, E, ID] =>

  def toDto(d: DAO): E = daoToDtoConverter.convert(d)

  def toDao(d: E): DAO = dtoToDaoConverter.convert(d)

  def toDtoPage(daoFlux: Flux[DAO], pageable: Pageable, totalSupplier: Mono[JLong]): Mono[Page[E]] = {
    fluxToPage(daoFlux.map(toDto), pageable, totalSupplier)
  }

  //=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  //=-=- Page / Sort Utilities
  //=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
    * Creates a Page response from the given flux
    *
    * @param contentFlux   the content as a flux
    * @param pageable      the pageable
    * @param totalSupplier the total supplier
    * @tparam T the result type
    * @return a Page result as a mono
    */
  protected def fluxToPage(contentFlux: Flux[E], pageable: Pageable, totalSupplier: Mono[JLong]): Mono[Page[E]] = {

    Objects.requireNonNull(pageable, "Pageable must not be null!")
    Objects.requireNonNull(totalSupplier, "TotalSupplier must not be null!")

    contentFlux.collectList().flatMap(lst => listToPage(lst, pageable, totalSupplier))
  }

  /**
    * Creates a Page response from the given content list
    *
    * @param content       the content
    * @param pageable      the pageable
    * @param totalSupplier the total supplier
    * @tparam T the content type
    * @return the Page result as a Mono
    */
  protected def listToPage(content: java.util.List[E], pageable: Pageable, totalSupplier: Mono[JLong]): Mono[Page[E]] = {

    Assert.notNull(content, "Content must not be null!")

    // get the size of the content
    val contentSize = content.size()

    // we can avoid executing the totalSupplier in several situations
    // so let's check now and improve the performance
    val total: Mono[JLong] = pageable match {
      // result has no pagination data
      case p: Pageable if p.isUnpaged => Mono.just(contentSize)

      // we're on the first page of data, and our result size is smaller than the page size
      case p: Pageable if p.getOffset == 0 && p.getPageSize > contentSize => Mono.just(contentSize)

      // we're NOT on the first page of data, we have some content, but the content size < page size
      case p: Pageable if p.getOffset > 0 && contentSize > 0 && p.getPageSize > contentSize =>
        Mono.just(pageable.getOffset + content.size)

      // all other cases, we'll need to execute the total supplier
      case _ => totalSupplier
    }

    // map the total Mono into a Page result
    total.map((total: JLong) => new PageImpl(content, pageable, total))
  }


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // Paging/Sorting Utilities
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
    * Create a PageRequest from the index and size criteria
    *
    * @param pageIndex the zero-based page index
    * @param pageSize  the page size
    * @return the PageRequest
    */
  protected def toPageRequest(pageIndex: Int, pageSize: Int): PageRequest = {
    toPageRequest(pageIndex, pageSize, Array.empty[String])
  }

  /**
    * Create a PageRequest from the index, size, and sort criteria
    *
    * @param pageIndex the zero-based page index
    * @param pageSize  the page size
    * @param sort      the sort strings
    * @return the PageRequest
    */
  protected def toPageRequest(pageIndex: Int, pageSize: Int, sort: String): PageRequest = {
    toPageRequest(pageIndex, pageSize, Array(sort))
  }

  /**
    * Create a PageRequest from the index, size, and sort criteria
    *
    * @param pageIndex the zero-based page index
    * @param pageSize  the page size
    * @param sort      the sort strings
    * @return the PageRequest
    */
  protected def toPageRequest(pageIndex: Int, pageSize: Int, sort: Array[String]): PageRequest = {
    val sortOptions = toSort(sort.mkString(StringUtils.COMMA))
    PageRequest.of(pageIndex, pageSize, sortOptions)
  }

  /**
    * Turn a string into a Sort object.  This handles single and multi sort strings, using "-" to denote DESC
    * e.g.: description,-endDate
    *
    * @param sort the sort string
    * @return the sort
    */
  protected def toSort(sort: String): Sort = toSort(sort, StringUtils.EMPTY)

  /**
    * Turn a string into a Sort object.  This handles single and multi sort strings, using "-" to denote DESC
    * e.g.: description,-endDate
    *
    * @param sort    the sort string
    * @param default the default sort string
    * @return the sort
    */
  protected def toSort(sort: String, default: String): Sort = {
    toOrder(StringUtils.nonEmpty(sort, trim = true).getOrElse(default)) match {
      case Nil => Sort.unsorted()
      case lst: List[Order] => Sort.by(lst: _*)
    }
  }

  /**
    * Turns a single string into an Order object
    *
    * @param sort the sort string
    * @return the Order
    */
  protected def toOrder(sort: String): List[Order] = {
    // get non-empty sort
    StringUtils.nonEmpty(sort, trim = true) match {

      // no criteria passed in
      case None => List.empty

      // multi-sort
      case Some(s) if s.contains(StringUtils.COMMA) =>
        s.split(StringUtils.COMMA).map(_.trim).toList.flatMap(toOrder)

      // single DESC sort
      case Some(s) if s.startsWith(StringUtils.Delimiters.DASH) =>
        List(Order.desc(s.drop(1)))

      // single ASC sort
      case Some(s) => List(Order.asc(s))
    }
  }


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // Scala Implicit Classes / Utilities
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
    * Implicit helper function to turn a function into a converter interface
    *
    * @param fn the function
    * @tparam A the source type
    * @tparam B the target type
    * @return the Converter instance
    */
  implicit def thunkToConverter[A, B](fn: A => B): Converter[A, B] = (source: A) => fn(source)

  /**
    * Implicitly wraps a DTO object and adds a toDao method
    *
    * @param dto the dto to wrap
    */
  implicit class WrappedDto(dto: E) {
    def toDao: DAO = Option(dto).map(d => dtoToDaoConverter.convert(d)).getOrElse(None.orNull.asInstanceOf[DAO])
  }

  /**
    * Implicitly wraps a DAO object to add a toDto method
    *
    * @param dao the dao to wrap
    */
  implicit class WrappedDao(dao: DAO) {
    def toDto: E = Option(dao).map(d => daoToDtoConverter.convert(d)).getOrElse(None.orNull.asInstanceOf[E])
  }

  /**
    * Implicitly wraps an Option[DAO] to add a toDto method
    *
    * @param dao the dao to wrap
    */
  implicit class WrappedDaoMono(dao: Mono[DAO]) {
    def toDto: Mono[E] = dao.mapNotNull(d => daoToDtoConverter.convert(d))
  }

  /**
    * Implicitly wraps a Mono[E] to add a toDao method
    *
    * @param dto the dto to wrap
    */
  implicit class WrappedDtoMono(dto: Mono[E]) {
    def toDao: Mono[DAO] = dto.mapNotNull(d => dtoToDaoConverter.convert(d))
  }

  /**
    * Implicitly wraps a Flux[DAO] to provide toDto and toPage methods
    *
    * @param dao the dao to wrap
    */
  implicit class WrappedDaoFlux(dao: Flux[DAO]) {
    def toDto: Flux[E] = dao.mapNotNull(d => daoToDtoConverter.convert(d))

    def toPage(pageable: Pageable, totalSupplier: Mono[JLong]): Mono[Page[E]] = {
      toDtoPage(dao, pageable, totalSupplier)
    }
  }

  /**
    * Implicitly wraps a Flux[DTO] to provide a toDao method
    *
    * @param dto the dto flux
    */
  implicit class WrappedDtoFlux(dto: Flux[E]) {
    def toDao: Flux[DAO] = dto.mapNotNull(d => dtoToDaoConverter.convert(d))

    def toPage(pageable: Pageable, totalSupplier: Mono[JLong]): Mono[Page[E]] = {
      fluxToPage(dto, pageable, totalSupplier)
    }
  }
}
