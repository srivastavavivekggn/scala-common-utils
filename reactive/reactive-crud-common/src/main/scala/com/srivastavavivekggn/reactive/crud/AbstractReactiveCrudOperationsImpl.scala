package com.srivastavavivekggn.scala.reactive.crud

import com.srivastavavivekggn.scala.util.TypeAlias.JBoolean
import com.srivastavavivekggn.scala.util.collection.CollectionUtils
import com.srivastavavivekggn.scala.util.domain.BaseModel
import org.reactivestreams.Publisher
import org.springframework.core.convert.converter.Converter
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.{Flux, Mono}

import java.util.Objects

/**
  * Base class for specific ReactiveCrudOperations implementations (e.g., Mongo, JPA, etc.)
  *
  * @tparam DAO the DAO (db specific) type
  * @tparam E   the entity type
  * @tparam ID  the entity ID type
  */
abstract class AbstractReactiveCrudOperationsImpl[DAO <: BaseModel[ID], E <: BaseModel[ID], ID <: java.io.Serializable]
  extends ReactiveCrudOperations[E, ID]
    with ReactiveCrudOperationsImplHelpers[DAO, E, ID] {

  /**
    * Require a reactrive repository implementation
    *
    * @return the repository
    */
  protected def repository: ReactiveCrudRepository[DAO, ID]

  /**
    * Converter instance to convert DAO to DTO
    *
    * @return the converter
    */
  protected def daoToDtoConverter: Converter[DAO, E]

  /**
    * Converter instance to convert DTO to DAO
    *
    * @return the converter
    */
  protected def dtoToDaoConverter: Converter[E, DAO]

  /**
    * Tries to find a single entity by it's ID
    *
    * @param id the entity id
    * @return the entity if found
    */
  override def findById(id: ID): Mono[E] = {
    Objects.requireNonNull(id, "id cannot be null for findById")
    repository.findById(id).toDto
  }

  /**
    * Find all entities in this collection
    *
    * @return the flux of content
    */
  override def findAll: Flux[E] = repository.findAll().toDto

  /**
    * Find multiple by ID
    *
    * @param ids the ids to find
    * @return the flux of results
    */
  override def findAllById(ids: java.lang.Iterable[ID]): Flux[E] = {
    Objects.requireNonNull(ids, "ids cannot be null for findAll")

    // if we have any ids present
    if (ids.iterator().hasNext) {
      repository.findAllById(Flux.fromIterable(ids)).toDto
    }
    // otherwise we can return an empty flux
    else {
      Flux.empty()
    }
  }

  /**
    * Find multiple by ID
    *
    * @param ids the ids to find
    * @return the list of results
    */
  override def findAllById(ids: Iterable[ID]): Flux[E] = findAllById(CollectionUtils.asJavaListOrEmpty(ids.toList))

  /**
    * Save the entity to the data store
    *
    * @param entity the entity to save
    * @return the saved entity
    */
  override def save(entity: E): Mono[E] = repository.save(entity.toDao).toDto

  /**
    * Save multiple entities to the data store
    *
    * @param entities the java iterable of entities to save
    * @return the flux of results
    */
  override def saveAll(entities: java.lang.Iterable[E]): Flux[E] = {
    Objects.requireNonNull(entities, "entities cannot be null for saveAll")

    if (entities.iterator().hasNext) {
      repository.saveAll(Flux.fromIterable(entities).toDao).toDto
    }
    else {
      Flux.empty()
    }
  }

  /**
    * Save multiple entities to the data store
    *
    * @param entities the scala iterable of entities to save
    * @return the flux of results
    */
  override def saveAll(entities: Iterable[E]): Flux[E] = saveAll(CollectionUtils.asJavaListOrEmpty(entities.toList))

  /**
    * Remove an item from the repository
    *
    * @param entity the entity to remove
    * @return an empty Future
    */
  override def delete(entity: E): Mono[Void] = repository.delete(entity.toDao)

  /**
    * Remove by id
    *
    * @param id the id to remove
    * @return an empty future
    */
  override def deleteById(id: ID): Mono[Void] = repository.deleteById(id)

  /**
    * Remove all entities
    *
    * @param entities the entities to remove
    * @return an empty mono
    */
  override def deleteAll(entities: java.lang.Iterable[E]): Mono[Void] = {
    Objects.requireNonNull(entities, "entities cannot be null for deleteAll")

    if (entities.iterator().hasNext) {
      repository.deleteAll(Flux.fromIterable(entities).toDao)
    }
    else {
      Mono.empty()
    }
  }

  /**
    * Remove all entities
    *
    * @param entities the entities to remove
    * @return an empty mono
    */
  override def deleteAll(entities: Iterable[E]): Mono[Void] = deleteAll(CollectionUtils.asJavaListOrEmpty(entities.toList))

  /**
    * Remove all by id
    *
    * @param ids the ids to remove
    * @return an empty mono
    */
  override def deleteAllById(ids: java.lang.Iterable[ID]): Mono[Void] = {
    Objects.requireNonNull(ids, "ids cannot be null for deleteAllById")

    if (ids.iterator().hasNext) {
      repository.deleteAllById(ids)
    }
    else {
      Mono.empty()
    }
  }

  /**
    * Remove all by id
    *
    * @param ids the ids to remove
    * @return an empty mono
    */
  override def deleteAllById(ids: Iterable[ID]): Mono[Void] = deleteAllById(CollectionUtils.asJavaListOrEmpty(ids.toList))

  /**
    * See if an entity with the given ID exists
    *
    * @param id the id
    * @return true if it exists, false otherwise
    */
  override def exists(id: ID): Mono[JBoolean] = repository.existsById(id)

  /**
    * See if an entity with the given ID exists
    *
    * @param id the id
    * @return true if it exists, false otherwise
    */
  override def exists(id: Publisher[ID]): Mono[JBoolean] = repository.existsById(id)
}
