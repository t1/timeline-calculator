package com.github.t1

import com.github.t1.bulmajava.basic.Renderable
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.MediaType.TEXT_HTML
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.ext.MessageBodyWriter
import jakarta.ws.rs.ext.Provider
import java.io.OutputStream
import java.lang.reflect.Type

@Provider
@Produces(TEXT_HTML)
class RenderableMessageBodyWriter : MessageBodyWriter<Renderable> {
    override fun isWriteable(
        type: Class<*>,
        genericType: Type,
        annotations: Array<Annotation>,
        mediaType: MediaType
    ) = true

    override fun writeTo(
        renderable: Renderable,
        type: Class<*>,
        genericType: Type,
        annotations: Array<out Annotation>,
        mediaType: MediaType,
        httpHeaders: MultivaluedMap<String, in Any>,
        entityStream: OutputStream
    ) {
        renderable.render(entityStream)
    }
}
