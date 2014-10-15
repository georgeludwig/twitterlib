package com.sixbuilder.twitterlib.services;

import com.google.gson.JsonObject;

public interface QueueManager {

    /**
     * Create a new queue document.
     * @param documentId document id
     * @throws Exception 
     */
    JsonObject create(String documentId) throws Exception;

    /**
     * Update an existing queue document.
     * @param documentId queue document id
     * @param queue queue as json string
     * @throws Exception 
     */
    void update(String documentId, JsonObject queue) throws Exception;

    /**
     * Retrieves a queue with a given documentId
     * @param documentId document id
     * @return queue as jsonString
     * @throws Exception 
     */
    JsonObject get(String documentId) throws Exception;

    /**
     * Removes a document for a given document id.
     * @param  queue
     */
    void remove(JsonObject queue);

}
