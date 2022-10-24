package com.potus.app;

import com.potus.app.airquality.repository.RegionRepository;
import com.potus.app.airquality.service.AirQualityService                            ;
import com.potus.app.potus.repository.PotusRepository;
import com.potus.app.potus.service.PotusEventsService;
import com.potus.app.potus.utils.PotusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class AppApplication {

	public static void main(String[] args) { SpringApplication.run(AppApplication.class, args); }
}

/*public class AppApplication {

	public static void main(String[] args) {
		//AirQualityService aqs = new AirQualityService();
		//aqs.InitializeRegions();
		System.out.println(PotusUtils.euclideanDistance(4.0, 4.0, 6.0, 6.0));
		//PotusEventsService pes = new PotusEventsService();
		//pes.getClosestRegions(1.0, 1.0);

	}
} */

