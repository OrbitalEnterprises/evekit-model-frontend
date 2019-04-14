package enterprises.orbital.evekit.frontend;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import javax.ws.rs.core.Application;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.DBPropertyProvider;
import enterprises.orbital.evekit.ws.character.ModelCharacterWS;
import enterprises.orbital.evekit.ws.common.ModelAccessKeyWS;
import enterprises.orbital.evekit.ws.common.ModelCommonWS;
import enterprises.orbital.evekit.ws.common.ModelMetaWS;
import enterprises.orbital.evekit.ws.corporation.ModelCorporationWS;
import enterprises.orbital.evekit.ws.evekitf.EveKitFCharacterWS;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.converter.ModelConverters;
import io.swagger.jackson.AbstractModelConverter;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.DateTimeProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.Property;
import io.swagger.util.Json;
import org.joda.time.DateTime;

public class ModelFrontendApplication extends Application {
  // Property which holds the name of the persistence unit for properties
  public static final String PROP_PROPERTIES_PU = "enterprises.orbital.evekit.model-frontend.properties.persistence_unit";
  @SuppressWarnings("unused")
  public static final String PROP_APP_PATH      = "enterprises.orbital.evekit.model-frontend.apppath";
  @SuppressWarnings("unused")
  public static final String DEF_APP_PATH       = "http://localhost/model";

  // ModelConverter which converts BigDecimal to Double instead
  public class BigDecimalModelConverter extends AbstractModelConverter {

    BigDecimalModelConverter() {
      super(Json.mapper());
    }

    @Override
    public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations, Iterator<ModelConverter> chain) {
      JavaType _type = Json.mapper().constructType(type);
      if (_type != null) {
        Class<?> cls = _type.getRawClass();
        if (BigDecimal.class.isAssignableFrom(cls)) {
          return new DoubleProperty();
        }
      }
      if (chain.hasNext()) {
        return chain.next().resolveProperty(type, context, annotations, chain);
      } else {
        return null;
      }
    }

    @Override
    public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> next) {
      if (next.hasNext()) {
        return next.next().resolve(type, context, next);
      } else {
        return null;
      }
    }
  }

  public ModelFrontendApplication() throws IOException {
    // Populate properties
    OrbitalProperties.addPropertyFile("EveKitModelFrontend.properties");
    // Sent persistence unit for properties
    PersistentProperty.setProvider(new DBPropertyProvider(OrbitalProperties.getGlobalProperty(PROP_PROPERTIES_PU)));
    // Override BigDecimal representation
    ModelConverters.getInstance().addConverter(new BigDecimalModelConverter());
  }

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> resources = new HashSet<>();
    // Model APIresources
    resources.add(ModelAccessKeyWS.class);
    resources.add(ModelMetaWS.class);
    resources.add(ModelCommonWS.class);
    resources.add(ModelCharacterWS.class);
    resources.add(ModelCorporationWS.class);
    // EveKitF
    resources.add(EveKitFCharacterWS.class);
    // Swagger additions
    resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
    resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

    return resources;
  }

}
