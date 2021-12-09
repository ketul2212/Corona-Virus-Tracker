package com.ketul.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ketul.module.CoronaVirusTracks;

@Service
public class CoronaVirusInfoService {

	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<CoronaVirusTracks> allList = new ArrayList<CoronaVirusTracks>();

    public List<CoronaVirusTracks> getAllStats() {
        return allList;
    }
    
    @PostConstruct
    @Scheduled(cron = "* * * * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
    	
        List<CoronaVirusTracks> tempList = new ArrayList<CoronaVirusTracks>();
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        
        /*
         * Note: Second way to get request
         * 
         * HttpRequest requestAnother = HttpRequest.newBuilder(URI.create(VIRUS_DATA_URL)).build();
         */
        
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        
        for (CSVRecord record : records) {
        	CoronaVirusTracks locationStat = new CoronaVirusTracks();
        	
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            
            long latestCases = Long.parseLong(record.get(record.size() - 1));
            long prevDayCases = Long.parseLong(record.get(record.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
            
            tempList.add(locationStat);
        }
        this.allList = tempList;
    }
    
   
}
