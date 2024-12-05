package com.github.t1

import com.charleskorn.kaml.encodeToStream
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyWriter
import jakarta.ws.rs.ext.Provider
import java.io.OutputStream
import java.lang.reflect.Type

@Provider
@Produces("application/yaml")
class YamlMessageBodyWriter : MessageBodyWriter<Project> {
    companion object {
        const val APPLICATION_YAML = "application/yaml"
    }

    override fun isWriteable(
        type: Class<*>,
        genericType: Type,
        annotations: Array<out Annotation>,
        mediaType: MediaType
    ) = true

    override fun writeTo(
        t: Project,
        type: Class<*>,
        genericType: Type,
        annotations: Array<out Annotation>,
        mediaType: MediaType,
        httpHeaders: MultivaluedMap<String, in Any>,
        entityStream: OutputStream
    ) {
        YAML.encodeToStream(t, entityStream)
    }
}
