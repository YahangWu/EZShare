package EZShare;

import JSON.JSONReader;
import Resource.HashDatabase;
import Resource.Resource;
import exceptions.InvalidResourceException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Created by Yahang Wu on 2017/4/16.
 * COMP90015 Distributed System Project1 EZServer
 * Server publish function to publish resources
 */
class Publish {
    /**
     * Validates and inserts a resource into the database for future sharing, which is not a file.
     *
     * @param resource The resource to be published.
     * @param db       The database the resource should be inserted into.
     * @throws InvalidResourceException If the resource supplied contains illegal fields, this is thrown.
     */
    static void publish(JSONReader resource, HashDatabase db) throws InvalidResourceException {
        String name = resource.getResourceName();
        String description = resource.getResourceDescription();
        String channel = resource.getResourceChannel();
        String owner = resource.getResourceOwner();
        String uri = resource.getResourceUri();
        String[] tags = resource.getResourceTags();
        String ezserver = resource.getResourceEZserver();

        //Check strings etc. are valid
        if (!Common.validateResource(name, description, tags, uri, channel, owner)) {
            throw new InvalidResourceException("Trying to publish Resource with illegal fields.");
        }
        try {
            URI path = new URI(uri);
            if (!path.isAbsolute() || path.getScheme().equals("file")) {
                throw new InvalidResourceException("Trying to publish resource with non-absolute or file uri.");
            }
        } catch (URISyntaxException e) {
            throw new InvalidResourceException("Attempting to publish resource with invalid uri syntax.");
        }

        //Make sure matching primary key resources are removed.
        Resource match = db.pKeyLookup(channel, uri);
        if (match != null) {
            db.deleteResource(match);
        }

        //Add resource to database.
        db.insertResource(new Resource(name, description, Arrays.asList(tags),
                uri, channel, owner, ezserver));
    }
}