package io.matheusvictor;

import io.matheusvictor.dto.UrlShortenerDTO;
import io.matheusvictor.dto.UrlShortenerResponseDTO;
import io.matheusvictor.entity.UrlShortener;
import io.matheusvictor.service.UrlShortenerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@Path("/short")
public class UrlShortenerResource {
    private static final Logger log = LoggerFactory.getLogger(UrlShortenerResource.class);
    @Inject
    UrlShortenerService urlShortenerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{url}")
    public Response urlShortener(@PathParam("url") String shortUrl) {
        String originalUrl = urlShortenerService.getShortenedUrl(shortUrl);
        log.info("Shortened URL: {}", originalUrl);

        try {
            if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                originalUrl = "https://" + originalUrl;
            }

            URI externalUri = URI.create(originalUrl);
            log.info("Redirecting to external URL: {}", externalUri);

            return Response.seeOther(externalUri).build();
        } catch (Exception e) {
            log.error("Error redirecting to external URL", e);
            throw new RuntimeException("Failed to redirect to external URL", e);
        }
    }



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response urlShortenerPost(UrlShortenerDTO urlShortener) {
        String response = urlShortenerService.shortenUrl(urlShortener.url());
        return Response.ok(new UrlShortenerResponseDTO(response)).build();
    }
}
