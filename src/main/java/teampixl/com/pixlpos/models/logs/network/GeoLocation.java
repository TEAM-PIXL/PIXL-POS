package teampixl.com.pixlpos.models.logs.network;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import com.maxmind.geoip2.record.City;

public class GeoLocation {

    protected static ServerLocation getLocation(String ipAddress) throws IOException, GeoIp2Exception {
        ClassLoader classLoader = GeoLocation.class.getClassLoader();
        File dbfile = new File(Objects.requireNonNull(classLoader.getResource("teampixl/com/pixlpos/database/GeoLite2-City.mmdb")).getFile());
        DatabaseReader dbReader = new DatabaseReader.Builder(dbfile).build();

        CityResponse response = dbReader.city(InetAddress.getByName(ipAddress));

        Country country = response.getCountry();
        Subdivision subdivision = response.getMostSpecificSubdivision();
        City city = response.getCity();

        ServerLocation serverLocation = new ServerLocation();
        serverLocation.setCountry(country.getName());
        serverLocation.setRegion(subdivision.getName());
        serverLocation.setCity(city.getName());

        return serverLocation;
    }

    protected static String displayLocation(ServerLocation serverLocation) {
        return  serverLocation.getCountry() + ", " + serverLocation.getRegion() + ", " + serverLocation.getCity();
    }
}