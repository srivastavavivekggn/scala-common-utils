package com.srivastavavivekggn.scala.reactive.crud

import com.srivastavavivekggn.scala.util.TypeAlias.JBoolean
import com.srivastavavivekggn.scala.util.domain.BaseModel
import org.reactivestreams.Publisher
import org.springframework.data.domain.{Page, PageRequest, Pageable}
import reactor.core.publisher.{Flux, Mono}

/**
  * A common set of crud-related operations as exposed to business services.  This interface deals solely
  * with the "DTO" or business entity.  The specific implementations
  * (i.e., those classes implementing AbstractReactiveCrudOperationsImpl) are aware of the distinction between
  * DTO and DAO (the latter being the database-specific entity definition)
  *
  * @tparam E the entity type
  * @tparam I the ID type
  */
trait ReactiveCrudOperations[E <: BaseModel[ID], ID <: java.io.Serializable] {

  /**
    * Default max page size
    */
  protected val DEFAULT_MAX_SIZE: Int = 100

  /**
    * Default page request
    */
  protected val DEFAULT_PAGE_REQUEST: Pageable = PageRequest.of(0, DEFAULT_MAX_SIZE)

  /**
    * Tries to find a single entity by it's ID
    *
    * @param id the entity id
    * @return the entity if found
    */
  def findById(id: ID): Mono[E]

  /**
    * Find all entities in this collection
    *
    * @return the flux of content
    */
  def findAll: Flux[E]

  /**
    * Initiate a findAll utilizing pagination
    *
    * @param pageIndex the page index
    * @param pageSize  the page size
    * @param sort      the sort criteria
    * @return the Flux of entity results
    */
  def findAll(pageIndex: Int, pageSize: Int, sort: Array[String]): Mono[Page[E]]

  /**
    * Initiate a findAll utilizing pagination
    *
    * @param pageIndex the page index
    * @param pageSize  the page size
    * @param sort      the sort criteria
    * @return the Flux of entity results
    */
  def findAll(pageIndex: Int, pageSize: Int, sort: String): Mono[Page[E]] = findAll(pageIndex, pageSize, Array(sort))

  /**
    * Initiate a findAll utilizing pagination
    *
    * @param pageIndex the page index
    * @param pageSize  the page size
    * @return the Flux of entity results
    */
  def findAll(pageIndex: Int, pageSize: Int): Mono[Page[E]] = findAll(pageIndex, pageSize, Array.empty[String])

  /**
    * Find multiple by ID
    *
    * @param ids the ids to find
    * @return the flux of results
    */
  def findAllById(ids: java.lang.Iterable[ID]): Flux[E]

  /**
    * Find multiple by ID
    *
    * @param ids the ids to find
    * @return the list of results
    */
  def findAllById(ids: Iterable[ID]): Flux[E]

  /**
    * Save the entity to the data store
    *
    * @param entity the entity to save
    * @return the saved entity
    */
  def save(entity: E): Mono[E]

  /**
    * Save multiple entities to the data store
    *
    * @param entities the java iterable of entities to save
    * @return the flux of results
    */
  def saveAll(entities: java.lang.Iterable[E]): Flux[E]

  /**
    * Save multiple entities to the data store
    *
    * @param entities the scala iterable of entities to save
    * @return the flux of results
    */
  def saveAll(entities: Iterable[E]): Flux[E]

  /**
    * Remove an item from the repository
    *
    * @param entity the entity to remove
    * @return an empty Future
    */
  def delete(entity: E): Mono[Void]

  /**
    * Remove by id
    *
    * @param id the id to remove
    * @return an empty future
    */
  def deleteById(id: ID): Mono[Void]

  /**
    * Remove all entities
    *
    * @param entities the entities to remove
    * @return an empty mono
    */
  def deleteAll(entities: java.lang.Iterable[E]): Mono[Void]

  /**
    * Remove all entities
    *
    * @param entities the entities to remove
    * @return an empty mono
    */
  def deleteAll(entities: Iterable[E]): Mono[Void]

  /**
    * Remove all by id
    *
    * @param id the ids to remove
    * @return an empty mono
    */
  def deleteAllById(ids: java.lang.Iterable[ID]): Mono[Void]

  /**
    * Remove all by id
    *
    * @param id the ids to remove
    * @return an empty mono
    */
  def deleteAllById(ids: Iterable[ID]): Mono[Void]

  /**
    * See if an entity with the given ID exists
    *
    * @param id the id
    * @return true if it exists, false otherwise
    */
  def exists(id: ID): Mono[JBoolean]

  /**
    * See if an entity with the given ID exists
    *
    * @param id the id
    * @return true if it exists, false otherwise
    */
  def exists(id: Publisher[ID]): Mono[JBoolean]
}
