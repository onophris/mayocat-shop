package org.mayocat.shop.rest.resources;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.configuration.shop.ShopConfiguration;
import org.mayocat.shop.model.Role;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.service.ConfigurationService;
import org.mayocat.shop.service.NoSuchModuleException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Multimap;
import com.yammer.metrics.annotation.Timed;

@Component("ConfigurationResource")
@Path("/configuration/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@Authorized
public class ConfigurationResource implements Resource
{
    @Inject
    private Logger logger;

    @Inject
    private ConfigurationService configurationService;

    @GET
    @Timed
    public Map<String, Object> getConfiguration()
    {
        return configurationService.getConfiguration();
    }

    @GET
    @Timed
    @Path("{module}")
    public Map<String, Object> getModuleConfiguration(@PathParam("module") String module)
    {
        try {
            return configurationService.getConfiguration(module);
        } catch (NoSuchModuleException e) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("No such module could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }

    @PUT
    @Timed
    @Path("{module}")
    @Authorized(roles = Role.ADMIN)
    public Response updateModuleConfiguration(@PathParam("module") String module, Map<String, Object> configuration)
    {
        try {
            configurationService.updateConfiguration(module, configuration);
            return Response.noContent().build();
        }
        catch (NoSuchModuleException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No such module could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @PUT
    @Timed
    @Authorized(roles = Role.ADMIN)
    public Response updateModuleConfiguration(Map<String, Object> configuration)
    {
        configurationService.updateConfiguration(configuration);
        return Response.noContent().build();
    }
}
