package com.hsm.barcode.easydl;

import android.content.Context;

//import android.util.Log;

/**
 * @brief This class is used to convert raw barcode data into a parsed, easy to
 *        use LicenseData object
 */
public class LicenseParser {
	private static String TAG = LicenseParser.class.getSimpleName();
	private static String SEPERATOR = ":" ;//"#%#";
	private static String VERSION = "";
//	static {
//		System.loadLibrary("native-lib");
//	}

	
	private static native String NativeParseRawData(Context ctx, byte[] rawData, int length);

	//private static native String NativeGetVersion(byte[] inputData, int length);

	/**
	 * This method returns the version
	 *
	 * @return LicenseParser version
	 */
	public static String getVersion()
	{
		//byte[] baData = inputData.getBytes();
		//return NativeGetVersion(baData, baData.length);
		return APIVersion.getVersion();
	}

	/**
	 * @brief This method accepts raw barcode data and returns a LicenseData
	 *        class instance that contains the parsed driver's license data
	 * 
	 * @param ctx Android context
	 * @param dataToParse Raw barcode data
	 * 
	 * @return A LicenseData class instance that contains the parsed driver's
	 *         license data
	 */
	public static LicenseData parseRawData(String licString) 	{
		
		
		LicenseData licData = new LicenseData();
		//Log.d(TAG, " final parsed string:" + licString);
		
		
		if (licString == null)
			return null;
		
	    java.util.HashMap<String, String> IIN_State = new java.util.HashMap<String, String>();
	    java.util.HashMap<String, String> State_Country = new java.util.HashMap<String, String>();
	    // Fill in IIN to State translation map
	    IIN_State.put("604426","PE");
	    IIN_State.put("604427","AS");
	    IIN_State.put("604428","QC");
	    IIN_State.put("604429","YT");
	    IIN_State.put("604430","MP");
	    IIN_State.put("604431","PR");
	    IIN_State.put("604432","AB");
	    IIN_State.put("604433","NU");
	    IIN_State.put("636000","VA");
	    IIN_State.put("636001","NY");
	    IIN_State.put("636002","MA");
	    IIN_State.put("636003","MD");
	    IIN_State.put("636004","NC");
	    IIN_State.put("636005","SC");
	    IIN_State.put("636006","CT");
	    IIN_State.put("636007","LA");
	    IIN_State.put("636008","MT");
	    IIN_State.put("636009","NM");
	    IIN_State.put("636010","FL");
	    IIN_State.put("636011","DE");
	    IIN_State.put("636012","ON");
	    IIN_State.put("636013","NS");
	    IIN_State.put("636014","CA");
	    IIN_State.put("636015","TX");
	    IIN_State.put("636016","NF");
	    IIN_State.put("636017","NB");
	    IIN_State.put("636018","IA");
	    IIN_State.put("636019","GU");
	    IIN_State.put("636020","CO");
	    IIN_State.put("636021","AR");
	    IIN_State.put("636022","KS");
	    IIN_State.put("636023","OH");
	    IIN_State.put("636024","VT");
	    IIN_State.put("636025","PA");
	    IIN_State.put("636026","AZ");
	    IIN_State.put("636028","BC");
	    IIN_State.put("636029","OR");
	    IIN_State.put("636030","MO");
	    IIN_State.put("636031","WI");
	    IIN_State.put("636032","MI");
	    IIN_State.put("636033","AL");
	    IIN_State.put("636034","ND");
	    IIN_State.put("636035","IL");
	    IIN_State.put("636036","NJ");
	    IIN_State.put("636037","IN");
	    IIN_State.put("636038","MN");
	    IIN_State.put("636039","NH");
	    IIN_State.put("636040","UT");
	    IIN_State.put("636041","ME");
	    IIN_State.put("636042","SD");
	    IIN_State.put("636043","DC");
	    IIN_State.put("636044","SK");
	    IIN_State.put("636045","WA");
	    IIN_State.put("636046","KY");
	    IIN_State.put("636047","HI");
	    IIN_State.put("636048","MB");
	    IIN_State.put("636049","NV");
	    IIN_State.put("636050","ID");
	    IIN_State.put("636051","MS");
	    IIN_State.put("636052","RI");
	    IIN_State.put("636053","TN");
	    IIN_State.put("636054","NE");
	    IIN_State.put("636055","GA");
	    IIN_State.put("636056","CU");
	    IIN_State.put("636057","HL");
	    IIN_State.put("636058","OK");
	    IIN_State.put("636059","AK");
	    IIN_State.put("636060","WY");
	    IIN_State.put("636061","WV");
	    IIN_State.put("636062","VI");
	    
	    // Fill in State to Country translation map
	    State_Country.put("AB","CAN");
	    State_Country.put("AK","USA");
	    State_Country.put("AL","USA");
	    State_Country.put("AR","USA");
	    State_Country.put("AS","USA");
	    State_Country.put("AZ","USA");
	    State_Country.put("BC","CAN");
	    State_Country.put("CA","USA");
	    State_Country.put("CO","USA");
	    State_Country.put("CT","USA");
	    State_Country.put("CU","MEX");
	    State_Country.put("DC","USA");
	    State_Country.put("DE","USA");
	    State_Country.put("FL","USA");
	    State_Country.put("GA","USA");
	    State_Country.put("GU","USA");
	    State_Country.put("HI","USA");
	    State_Country.put("HL","MEX");
	    State_Country.put("IA","USA");
	    State_Country.put("ID","USA");
	    State_Country.put("IL","USA");
	    State_Country.put("IN","USA");
	    State_Country.put("KS","USA");
	    State_Country.put("KY","USA");
	    State_Country.put("LA","USA");
	    State_Country.put("MA","USA");
	    State_Country.put("MB","CAN");
	    State_Country.put("MD","USA");
	    State_Country.put("ME","USA");
	    State_Country.put("MI","USA");
	    State_Country.put("MN","USA");
	    State_Country.put("MO","USA");
	    State_Country.put("MP","USA");
	    State_Country.put("MS","USA");
	    State_Country.put("MT","USA");
	    State_Country.put("NB","CAN");
	    State_Country.put("NC","USA");
	    State_Country.put("ND","USA");
	    State_Country.put("NE","USA");
	    State_Country.put("NF","CAN");
	    State_Country.put("NH","USA");
	    State_Country.put("NJ","USA");
	    State_Country.put("NM","USA");
	    State_Country.put("NS","CAN");
	    State_Country.put("NU","CAN");
	    State_Country.put("NV","USA");
	    State_Country.put("NY","USA");
	    State_Country.put("OH","USA");
	    State_Country.put("OK","USA");
	    State_Country.put("ON","CAN");
	    State_Country.put("OR","USA");
	    State_Country.put("PA","USA");
	    State_Country.put("PE","CAN");
	    State_Country.put("PR","USA");
	    State_Country.put("QC","CAN");
	    State_Country.put("RI","USA");
	    State_Country.put("SC","USA");
	    State_Country.put("SD","USA");
	    State_Country.put("SK","CAN");
	    State_Country.put("TN","USA");
	    State_Country.put("TX","USA");
	    State_Country.put("UT","USA");
	    State_Country.put("VA","USA");
	    State_Country.put("VI","USA");
	    State_Country.put("VT","USA");
	    State_Country.put("WA","USA");
	    State_Country.put("WI","USA");
	    State_Country.put("WV","USA");
	    State_Country.put("WY","USA");
	    State_Country.put("YT","CAN");

			String[] licArray = licString.split(SEPERATOR);
			// Log.d(TAG, " After string split : length :::"+licArray.length);
			// 24(ClassificationCode) &29(Uniquecustid) not able to map to
			// invision solution

			if (licArray.length > 1) {
				String lastname = "";
				String firstname = "";
				String middle = "";
				String suffix = "";
				String dlHcm ="";
				String dlname="";
				String dlHcmtoInches ="";
				//licData.DLIIN = licArray[91];
				try{
					licData.DLIIN = licArray[91]; 
				} catch(ArrayIndexOutOfBoundsException aiobex){
					System.out.println("ArrayIndexOutOfBoundsException : DL IIN field contains empty/nonsensical data !!!");
					licData.DLIIN = "";
				} finally {
					//TODO

				}
												
				//licData.DLMedicalCodes = "";
				try{
					licData.DLMedicalCodes = licArray[93]; 
				} catch(ArrayIndexOutOfBoundsException aiobex){
					System.out.println("ArrayIndexOutOfBoundsException : DL Medical codes field contains empty/nonsensical data !!!");
					licData.DLMedicalCodes = "";
				} finally {
					//TODO

				}
				
				licData.DLState = licArray[17];
				licData.DLMailState = licArray[17];				
				if(licData.DLState.isEmpty())
				{
					String State = IIN_State.get(licData.DLIIN);
					if(State != null)
					{
						licData.DLState = State;
						licData.DLMailState = licData.DLState;
					}	
				}
				
				//Log.d(TAG, " height in cm/in format index test:"+licArray[92]);
				try{
					dlHcm = licArray[92];
				} catch(ArrayIndexOutOfBoundsException aiobex){
					System.out.println("ArrayIndexOutOfBoundsException : DL Height field contains empty/nonsensical data !!!");
					dlHcm = "";
				} finally {
					//TODO

				}
				
				if(licData.DLState.equals("BC")||licData.DLState.equals("bc"))
				{
					if (dlHcm.trim().endsWith("cm")) {
						
						
						dlHcm = dlHcm.trim().substring(0, dlHcm.trim().length() - 2) + " ";
						int iend = dlHcm.indexOf(" ");
						if (iend != -1) 
						{
							dlHcmtoInches = dlHcm.substring(0 , iend); //this will give height..
							dlHcm = String.valueOf(Math.round((0.393701 * Integer.parseInt(dlHcmtoInches)))) + " "; //
							licData.DLHeightCM = dlHcm;
							licData.XHeightCentimeters = dlHcm;
						}
						String strCMToInches = licData.DLHeightCM;
						iend = strCMToInches.indexOf(" ");
						if (iend != -1) 
						{
							dlHcmtoInches = strCMToInches.substring(0 , iend); //this will give heigh
						//	Log.d(TAG," DL Height end with comma(cm)..so replaced with space::"+dlHcm);
							
							licData.DLHeight = String.valueOf(Math.round((0.393701 * Integer.parseInt(dlHcmtoInches)))) + " IN"; // IN =Inches
							licData.XHeightInches = String.valueOf(Math.round((0.393701 * Integer.parseInt(dlHcmtoInches))));
						}
						
						
					}else{
						
						try{
							dlHcm = licArray[92];
							String strCMToInches = dlHcm.trim();
							licData.DLHeightCM = licArray[92];
							licData.XHeightCentimeters = licArray[92];
							licData.DLHeight = String.valueOf(Math.round(0.393701 * Integer.parseInt(strCMToInches))) + " IN"; // IN =Inches
							licData.XHeightInches = String.valueOf(Math.round(0.393701 * Integer.parseInt(strCMToInches)));
							
						} catch(ArrayIndexOutOfBoundsException aiobex){
							System.out.println("ArrayIndexOutOfBoundsException : DL Height cm field contains empty/nonsensical data !!!");
							licData.DLHeightCM = "";
							licData.XHeightCentimeters = "";
							licData.DLHeight = "";
							licData.XHeightInches = "";
						} finally {
							

						}
					}
				}
				else{
					if (dlHcm.trim().endsWith("cm")) {
						
						dlHcm = dlHcm.trim().substring(0, dlHcm.trim().length() - 2) + " ";
						//Log.d(TAG," DL Height end with comma(cm)..so replaced with space::"+dlHcm);
						licData.DLHeightCM = dlHcm;
						licData.XHeightCentimeters = dlHcm;
						
					}else{
						
						try{
							licData.DLHeightCM = licArray[92];
							licData.XHeightCentimeters = licArray[92];
							
						} catch(ArrayIndexOutOfBoundsException aiobex){
							System.out.println("ArrayIndexOutOfBoundsException : DL Height cm field contains empty/nonsensical data !!!");
							licData.DLHeightCM = "";
							licData.XHeightCentimeters = "";
						} finally {
							//TODO

						}
					}
				}	
				// DL Name = Last+First+Middle+SUFFIX
				lastname = licArray[2] + ", ";
				firstname = licArray[0] + ", ";
				middle = licArray[1] +", " ;
				suffix =  licArray[6];
				licData.DLName = lastname + firstname + middle + suffix;
				
				/*if (licArray[2].isEmpty() == false && licArray[2].length() > 0) {
					lastname = licArray[2] + ", ";
				}

				if (licArray[0].isEmpty() == false && licArray[0].length() > 0) {
					firstname = licArray[0] + ", ";
				}

				if (licArray[1].isEmpty() == false && licArray[1].length() > 0) {
					middle = licArray[1];
				}

				if (licArray[6].isEmpty() == false && licArray[6].length() > 0) {
					suffix = ", " + licArray[6];
				} */
				
				//dlname = lastname + firstname + middle + suffix;
				//dlname = dlname.replaceAll("(, ,)", ",");
				//Log.d(TAG, " replace ,, with single comma::"+dlname);
				
				/*if (dlname.trim().endsWith(",")) {
					dlname = dlname.trim().substring(0, dlname.trim().length() - 1) + " ";
					//Log.d(TAG," DL Name end with comma(,)..so replaced with space::"+dlname);
				}*/
				//licData.DLName = dlname;
				
				licData.DLNameFirst = licArray[0];
				licData.DLNameMid = licArray[1];
				licData.DLNameLast = licArray[2];
				if(licData.DLState.equals("BC")||licData.DLState.equals("bc"))
				{
					licData.DLWeightKG = licArray[3].trim();
					try{

					licData.DLWeight =  String.valueOf(Math.round((2.20462 * (Integer.parseInt(licArray[3].trim())))));
					
					} catch(NumberFormatException nfex){
						System.out.println("NumberFormatException : DL Weightkg field contains  nonsensical data !!!");
						licData.DLWeightKG = "";
					} finally {
						//TODO

					}
				}
				else
				{
					licData.DLWeight = licArray[3];
					
					try{

						if (licArray[3].isEmpty() == false && licArray[3].length() >= 1) {
							licData.DLWeightKG = String.valueOf(Math.round((0.453592 * (Integer.parseInt(licArray[3])))));
						} else {
							licData.DLWeightKG = "";
						}

					} catch(NumberFormatException nfex){
						System.out.println("NumberFormatException : DL Weightkg field contains  nonsensical data !!!");
						licData.DLWeightKG = "";
					} finally {
						//TODO

					}
				}
							
			//	System.out.println("Next step : DL Weightkg field contains  nonsensical data !!!");
				// licData.DLHeight = String.valueOf(Integer.parseInt(licArray[4]))+" IN"; IN=Inches
				if(licData.DLState.equals("BC")||licData.DLState.equals("bc"))
				{
					// try{
						// if (licArray[4].isEmpty() == false && licArray[4].length() >= 1) {
													
							// licData.DLHeight = String.valueOf(Math.round((0.393701 * (Integer.parseInt(licArray[4]))))) + " IN"; // IN =Inches
							// licData.XHeightInches = String.valueOf(Math.round((0.393701 * (Integer.parseInt(licArray[4])))));
						// } else {
							// licData.DLHeight = "";
							// licData.XHeightInches = "";
						// }
					
					// } catch(NumberFormatException nfex){
						// System.out.println("NumberFormatException : DLHeight/XHeightInches field contains  nonsensical data !!!");
						// licData.DLHeight = "";
						// licData.XHeightInches = "";
					// } finally {
						//TODO
					  // }
	
				}
				else
				{
					try{
						if (licArray[4].isEmpty() == false && licArray[4].length() >= 1) {
							licData.DLHeight = String.valueOf(Integer.parseInt(licArray[4])) + " IN"; // IN =Inches
							licData.XHeightInches = String.valueOf(Integer.parseInt(licArray[4]));
						} else {
							licData.DLHeight = "";
							licData.XHeightInches = "";
						}
					
					} catch(NumberFormatException nfex){
						System.out.println("NumberFormatException : DLHeight/XHeightInches field contains  nonsensical data !!!");
						licData.DLHeight = "";
						licData.XHeightInches = "";
					} finally {
						//TODO
					  }
	
				}				
				licData.DLOrganDonor = licArray[5];
				licData.DLNameSfx = licArray[6];
				licData.DLNamePrfx = licArray[7];
				licData.DLMailAddress1 = licArray[8];
				licData.DLAddress1 = licArray[8];
				licData.DLMailAddress2 = licArray[9];
				licData.DLAddress2 = licArray[9];
				licData.DLPlaceOfBirth = licArray[10];
				licData.DLBirthDate = licArray[11]; // DOB
				licData.DLSSN = licArray[12];
				licData.DLMailPostalCode = licArray[13];
				licData.DLPostalCode = licArray[13];
				licData.DLSex = licArray[14];
				licData.DLEyes = licArray[15];
				licData.DLHair = licArray[16];

				
				licData.DLCountry = licArray[50];
							
				if(licData.DLCountry.isEmpty() && ( !(licData.DLState.isEmpty()) ) )
				{
					String Country = State_Country.get(licData.DLState);
					if(Country != null)
						licData.DLCountry = Country;
				}
				
				// 636027 IIN is not related to a specific state but to USA country
				if(licData.DLCountry.isEmpty() && licData.DLIIN.equalsIgnoreCase("636027") )
				{
					licData.DLCountry = "USA";
				}				 
				
				licData.DLMailCity = licArray[18];
				licData.DLCity = licArray[18];
				licData.DLCustomerID = licArray[19];
				licData.DLExpires = licArray[20];
				licData.DLIssueDate = licArray[21];
				licData.DLRaceEthnicity = licArray[22];
				licData.DLNumDuplicates = licArray[23];
				// index - 24 not able to map to Invision solution
				licData.DLEndorsements = licArray[25];
				//licData.DLRestrictions = licArray[26];
				if (licArray[26].isEmpty() == false && licArray[26].length() > 0) {
					licData.DLRestrictions = licArray[26];
				} else {
					licData.DLRestrictions = "NONE";
				}
				//licData.DLClass = licArray[27];
				if (licArray[27].isEmpty() == false && licArray[27].length() > 0) {
					licData.DLClass = licArray[27];
				} else {
					licData.DLClass = "NONE";
				}
				licData.DLVeteran = licArray[28];
				// index 29 not able to map to Invision solution
				licData.DLNonResident = licArray[30];
				licData.DLIssTimestamp = licArray[31];
				licData.PermitID = licArray[32];
				
				
				// licData.DLUnder18Until = licArray[33];
				if (licArray[33].isEmpty() == false
						&& licArray[33].length() > 2) {
					licData.DLUnder18Until = licArray[33];
				} else {
					// DLUnder18Until field is empty/blank from easyDL2.0. so we
					// will calculated & mapped based on DOB
					if (licArray[11].isEmpty() == false	&& licArray[11].length() > 2) {

						try{
							licData.DLUnder18Until = String.valueOf((Integer.parseInt(licArray[11]) + 180000));

						} catch(NumberFormatException nfex){
							System.out.println("NumberFormatException : DLUnder18Until or DOB field contains  nonsensical data !!!");
							licData.DLUnder18Until = "";
						} finally {
							//TODO
						}

					} else {
						// Both DOB & Under18 Date is empty/blank from EasyDL2.0
						licData.DLUnder18Until = licArray[33];
					}

				}

				// licData.DLUnder19Until = licArray[34];
				if (licArray[34].isEmpty() == false
						&& licArray[34].length() > 2) {
					licData.DLUnder19Until = licArray[34];
				} else {
					// DLUnder19Until field is empty/blank from easyDL2.0. so we
					// will calculated & mapped based on DOB
					if (licArray[11].isEmpty() == false	&& licArray[11].length() > 2) {

						try{
							licData.DLUnder19Until = String.valueOf((Integer.parseInt(licArray[11]) + 190000));
						} catch(NumberFormatException nfex){
							System.out.println("NumberFormatException : DLUnder19Until or DOB field contains  nonsensical data !!!");
							licData.DLUnder19Until = "";
						} finally {
							//TODO
						}

					} else {
						// Both DOB & Under19 Date is empty/blank from EasyDL2.0
						licData.DLUnder19Until = licArray[34];
					}

				}

				// licData.DLUnder21Until = licArray[35];
				if (licArray[35].isEmpty() == false
						&& licArray[35].length() > 2) {
					licData.DLUnder21Until = licArray[35];
				} else {
					// DLUnder21Until field is empty/blank from easyDL2.0. so we
					// will calculated & mapped based on DOB
					if (licArray[11].isEmpty() == false	&& licArray[11].length() > 2) {

						try{
							licData.DLUnder21Until = String.valueOf((Integer.parseInt(licArray[11]) + 210000));
						} catch(NumberFormatException nfex){
							System.out.println("NumberFormatException : DLUnder21Until or DOB field contains  nonsensical data !!!");
							licData.DLUnder21Until = "";
						} finally {
							//TODO
						}

					} else {
						// Both DOB & Under21 Date is empty/blank from EasyDL2.0
						licData.DLUnder21Until = licArray[35];
					}

				}
								
				
				licData.DLLimitedDuration = licArray[36];
				licData.DLHAZMATExpDate = licArray[37];
				licData.DLRevisionDate = licArray[38];
				licData.DLComplianceType = licArray[39];
				//licData.DLClassDesc = licArray[40];
				if(licData.DLState.equals("BC"))
				{
					licData.DLClassDesc = "";
				}
				else
				{
					licData.DLClassDesc = licArray[40];
				}

				// licData.DLEndorsementsDesc = licArray[41];
				if (licArray[41].isEmpty() == false
						&& licArray[41].length() > 0) {
					licData.DLEndorsementsDesc = licArray[41];
				} else {
					licData.DLEndorsementsDesc = "NONE";
				}

				// licData.DLRestrictionsDesc = licArray[42];
				if (licArray[42].isEmpty() == false
						&& licArray[42].length() > 0) {
					licData.DLRestrictionsDesc = licArray[42];
				} else {
					licData.DLRestrictionsDesc = "NONE";
				}

				licData.DLStdVehicleClass = licArray[43];
				licData.DLStdEndorsements = licArray[44];
				licData.DLStdRestrictions = licArray[45];
				licData.DLWeightRange = licArray[46];
				licData.DLInventoryCtrl = licArray[47];
				licData.DLAuditInfo = licArray[48];
				licData.DLDocumentDiscr = licArray[49];
				//licData.DLCountry = licArray[50];
				licData.DLNameTruncLast = licArray[51];
				licData.DLNameTruncFirst = licArray[52];
				licData.DLNameTruncMid = licArray[53];
				licData.DLIDNumber = licArray[54];

				// other fields not supported
				licData.DLAddress3 = "";
				
				
				
				
				licData.DLFedCommCodes = "";
				licData.XVersion = VERSION;
											
				licData.DLIDNumberExt = "";
				licData.DLTemplateVer = "";
				licData.DLSecurityVer = "";
				licData.DLReserved1 = "";
				//licData.DLReserved2 = "";
				try{
				licData.DLReserved2 = licArray[57];
				}
				catch(ArrayIndexOutOfBoundsException aiobex){
						
						licData.DLReserved2 = "";
						
					} finally {
						//TODO

					}
				//licData.DLSecurity = "";
				try{
				licData.DLSecurity = licArray[56];
				}
				catch(ArrayIndexOutOfBoundsException aiobex){
						
						licData.DLSecurity = "";
						
					} finally {
						//TODO

					}
				licData.PermitClass = "";
				licData.PermitExpDate = "";
				licData.PermitIssDate = "";
				licData.PermitRestrict = "";
				licData.PermitEndorse = "";

				licData.AKASSN = "";
				licData.AKAName = "";
				licData.AKANameLast = "";
				licData.AKANameFirst = "";
				licData.AKANameMid = "";
				licData.AKANameSfx = "";
				licData.AKANamePrfx = "";
				licData.AKABirthDate = "";

				// "RG" subfile Mandatory fields follow:
				licData.RGIssueDate = "";
				licData.RGExpires = "";
				licData.RGPlateNum = "";
				licData.RGNameLast = "";
				licData.RGNameFirst = "";
				licData.RGName = "";
				licData.RGNameMid = "";
				licData.RGNameSfx = "";
				licData.RGAddress = "";
				licData.RGCity = "";
				licData.RGPostalCode = "";
				licData.RGVIN = "";
				licData.RGMake = "";
				licData.RGModelYear = "";
				licData.RGBody = "";
				licData.RGRegYear = "";
				licData.RGDecalNum = "";

				// "RG" subfile Optional fields follow:
				licData.RGVehicleUse = "";
				licData.RGFuel = "";
				licData.RGAxles = "";
				licData.RGWeight = "";
				licData.RGModel = "";
				licData.RGBusinessName = "";
				licData.RGColor = "";

				// "IR" subfile fields follow:
				licData.IRUnit = "";
				licData.IRVehicleType = "";
				licData.IRSeats = "";
				licData.IRRegIssueDate = "";
				licData.IRDecalNum = "";
				licData.IREnforceDate = "";
				licData.IRBaseRegWeight = "";
				licData.IRInsCompanyCode = "";
				licData.IREffectiveDate = "";

				// not useful fields
				licData.NumFields = "";
				
					
				
			}

			return licData;
			
		}
	
}
