package enterprises.orbital.evekit.frontend;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.DBPropertyProvider;
import enterprises.orbital.evekit.ws.character.ModelCharacterWS;
import enterprises.orbital.evekit.ws.common.ModelCommonWS;
import enterprises.orbital.evekit.ws.common.ModelMetaWS;
import enterprises.orbital.evekit.ws.corporation.ModelCorporationWS;

public class ModelFrontendApplication extends Application {
  // Property which holds the name of the persistence unit for properties
  public static final String PROP_PROPERTIES_PU = "enterprises.orbital.evekit.model-frontend.properties.persistence_unit";
  public static final String PROP_APP_PATH      = "enterprises.orbital.evekit.model-frontend.apppath";
  public static final String DEF_APP_PATH       = "http://localhost/model";

  public ModelFrontendApplication() throws IOException {
    // Populate properties
    OrbitalProperties.addPropertyFile("EveKitModelFrontend.properties");
    // Sent persistence unit for properties
    PersistentProperty.setProvider(new DBPropertyProvider(OrbitalProperties.getGlobalProperty(PROP_PROPERTIES_PU)));
  }

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> resources = new HashSet<Class<?>>();
    // Model APIresources
    resources.add(ModelMetaWS.class);
    resources.add(ModelCommonWS.class);
    resources.add(ModelCharacterWS.class);
    resources.add(ModelCorporationWS.class);
    // Swagger additions
    resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
    resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
    // Return resource set
    return resources;
  }

}
