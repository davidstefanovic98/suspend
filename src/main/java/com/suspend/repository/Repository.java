package com.suspend.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {

    /**
     * Finds all the records in the database.
     * @return A list of objects of the given class.
     */
    List<T> findAll();

    /**
     * Saves the given model to the database.
     * @param model The model to save.
     * @return The saved model.
     */
    T save(T model);

    /**
     * Updates the given model in the database.
     * @param model The model to update.
     * @return The updated model.
     */
    T update(T model);

    /**
     * Finds a record by the given id.
     * @param id The id of the record to find.
     * @return The record with the given id.
     */
    Optional<T> findById(ID id);

    /**
     * Deletes a record by the given id.
     * @param id The id of the record to delete.
     */
    void deleteById(ID id);
}
