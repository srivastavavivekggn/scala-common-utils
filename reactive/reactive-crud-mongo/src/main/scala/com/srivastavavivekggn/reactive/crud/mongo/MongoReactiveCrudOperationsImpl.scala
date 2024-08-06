package com.srivastavavivekggn.scala.reactive.crud.mongo

import com.srivastavavivekggn.scala.reactive.crud.AbstractReactiveCrudOperationsImpl
import com.srivastavavivekggn.scala.util.domain.BaseModel
import org.springframework.data.domain.Page
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Mono

/**
  * Abstract implementation of the ReactiveCrudOperations specific to MongoDB
  *
  * @tparam DAO the DAO type
  * @tparam E   the Entity type
  * @tparam ID  the entity ID type
  */
abstract class MongoReactiveCrudOperationsImpl[DAO <: BaseModel[ID], E <: BaseModel[ID], ID <: java.io.Serializable]
  extends AbstractReactiveCrudOperationsImpl[DAO, E, ID] {

  /**
    * Re-usable empty query
    */
  private val emptyQuery = new Query()

  /**
    * Specify the DAO class type so we can make queries using mongo operations
    *
    * @return the dao class
    */
  def daoClass: Class[DAO]

  /**
    * Require an instance of ReactiveMongoOperations
    *
    * @return the mongo operations bean
    */
  def mongoOperations: ReactiveMongoOperations

  /**
    * Initiate a findAll utilizing pagination
    *
    * @param pageIndex the page index
    * @param pageSize  the page size
    * @param sort      the sort criteria
    * @return the Flux of entity results
    */
  def findAll(pageIndex: Int,
              pageSize: Int,
              sort: Array[String]): Mono[Page[E]] = {

    val pg = toPageRequest(pageIndex, pageSize, sort)

    mongoOperations
      .find(emptyQuery.`with`(pg), daoClass)
      .toPage(pg, mongoOperations.count(emptyQuery, daoClass))
  }

}
