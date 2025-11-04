package charron.projetprimejsp.Configurations;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Base64;

public class BytesToStringConverter implements Converter<byte[], String>
{
    @Override
    public String convert(MappingContext<byte[], String> context)
    {
        byte[] source = context.getSource();
        if (source == null) {
            return null;
        }
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedString = encoder.encodeToString(source);
        // definir le src de la balise img
        return "data:image/jpg;base64,"+encodedString;
    }

}
