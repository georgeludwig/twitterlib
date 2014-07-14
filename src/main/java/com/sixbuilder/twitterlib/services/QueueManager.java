package com.sixbuilder.twitterlib.services;

import com.google.gson.JsonObject;

public interface QueueManager {

    /**
     * Create a new queue document.
     * @param documentId document id
     */
    JsonObject create(String documentId);

    /**
     * Update an existing queue document.
     * @param documentId queue document id
     * @param queue queue as json string
     */
    void update(String documentId, JsonObject queue);

    /**
     * Retrieves a queue with a given documentId
     * @param documentId document id
     * @return queue as jsonString
     */
    JsonObject get(String documentId);

    /**
     * Removes a document for a given document id.
     * @param  queue
     */
    void remove(JsonObject queue);

}
