package com.sixbuilder.twitterlib.services;

import com.google.gson.JsonObject;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.services.Response;

import java.io.IOException;
import java.io.PrintWriter;

public class JsonObjectEventResultProcessor implements ComponentEventResultProcessor<JsonObject> {

    private final Response response;

    private final String outputEncoding;

    public JsonObjectEventResultProcessor(Response response,
        @Symbol(SymbolConstants.CHARSET)
        String outputEncoding) {
        this.response = response;
        this.outputEncoding = outputEncoding;
    }

    public void processResultValue(JsonObject value) throws IOException {
        ContentType contentType = new ContentType(InternalConstants.JSON_MIME_TYPE, outputEncoding);

        PrintWriter pw = response.getPrintWriter(contentType.toString());

        pw.write(value.toString());
        pw.close();
    }

}
