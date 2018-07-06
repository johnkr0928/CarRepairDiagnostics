package com.ubiquisoft.evaluation;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class CarDiagnosticEngine {

	public void executeDiagnostics(Car car) {
		if(null != car){
			Boolean diagnostic = true;
			//First   - Validate the 3 data fields are present
			if(null == car.getMake() || car.getMake() == ""){
				System.out.println("Car Make information missing");
				diagnostic = false;
			}
			if(null == car.getModel() || car.getModel() == ""){
				System.out.println("Car Model information missing");
				diagnostic = false;
			}
			if(null == car.getYear() || car.getYear() == ""){
				System.out.println("Car Year of information missing");
				diagnostic = false;
			}
			if(!diagnostic)	throw new IllegalArgumentException("Diagnostic failure due to incomplete information.");
			
			//Second  - Validate that no parts are missing using the 'getMissingPartsMap' method in the Car class
			Map<PartType, Integer> missingParts = car.getMissingPartsMap();
			if(missingParts.size() > 0){	
				Iterator<Map.Entry<PartType, Integer>> iterator = missingParts.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<PartType, Integer> pair = iterator.next();
				    printMissingPart(pair.getKey(), pair.getValue());
				}
				diagnostic = false;
			}
			if(!diagnostic)	throw new IllegalArgumentException("Diagnostic failure due to missing parts.");
			
			//Third   - Validate that all parts are in working condition
			for(Part part: car.getParts()){
				ConditionType condition = part.getCondition();
				if(condition == ConditionType.FLAT || condition == ConditionType.DAMAGED || condition == ConditionType.NO_POWER || condition == ConditionType.SIEZED || condition == ConditionType.CLOGGED){
					printDamagedPart(part.getType(), part.getCondition());
					diagnostic = false;
				}
			}
			if(!diagnostic)	throw new IllegalArgumentException("Diagnostic failure due to damaged parts.");
			
			//Fourth  - If validation succeeds for the previous steps then print something to the console informing the user as such.
			System.out.println("********Car Diagnostic Completed Successfully********");
	}

	private void printMissingPart(PartType partType, Integer count) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (count == null || count <= 0) throw new IllegalArgumentException("Count must be greater than 0");

		System.out.println(String.format("Missing Part(s) Detected: %s - Count: %s", partType, count));
	}

	private void printDamagedPart(PartType partType, ConditionType condition) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (condition == null) throw new IllegalArgumentException("ConditionType must not be null");

		System.out.println(String.format("Damaged Part Detected: %s - Condition: %s", partType, condition));
	}

	public static void main(String[] args) throws JAXBException {
		// Load classpath resource
		InputStream xml = ClassLoader.getSystemResourceAsStream("SampleCar.xml");

		// Verify resource was loaded properly
		if (xml == null) {
			System.err.println("An error occurred attempting to load SampleCar.xml");

			System.exit(1);
		}

		// Build JAXBContext for converting XML into an Object
		JAXBContext context = JAXBContext.newInstance(Car.class, Part.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		Car car = (Car) unmarshaller.unmarshal(xml);

		// Build new Diagnostics Engine and execute on deserialized car object.

		CarDiagnosticEngine diagnosticEngine = new CarDiagnosticEngine();

		diagnosticEngine.executeDiagnostics(car);

	}

}
