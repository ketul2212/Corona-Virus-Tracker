package com.ketul.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ketul.module.CoronaVirusTracks;
import com.ketul.service.CoronaVirusInfoService;

@Controller
public class CoronaVirusController {
	
	@Autowired
	private CoronaVirusInfoService coronaVirusInfoService;

	@GetMapping("/")
	public String coronaInfo(Model model) {
		
		List<CoronaVirusTracks> list = coronaVirusInfoService.getAllStats();
		
		long totalCases = 0, totalNewCases = 0;
		
		for(CoronaVirusTracks coronaVirusTracks : list) {
			totalCases += coronaVirusTracks.getLatestTotalCases();
			totalNewCases += coronaVirusTracks.getDiffFromPrevDay();
		}
		
		model.addAttribute("coronaVirusList", list);
        model.addAttribute("totalReportedCases", totalCases);
        model.addAttribute("totalNewCases", totalNewCases);
		
		return "home";
	}
}
