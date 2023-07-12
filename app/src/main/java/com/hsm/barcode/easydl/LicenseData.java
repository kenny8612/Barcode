package com.hsm.barcode.easydl;

import android.util.Log;

import java.lang.reflect.Field;

/**
 * @brief This class contains all information that can be parsed out of a
 *        driver's license.
 */
public class LicenseData {
	public LicenseData() {

	}
	private static final String TAG = "LicenseData.java";
	public static int FieldCount = 121;

	/**
	 * @brief Drivers License: Issuer Identification Number
	 * @details The issuer's 6 digit AAMVA indentification number.
	 */
	public String DLIIN;

	/**
	 * @brief Drivers License: Name
	 * @details Cardholder's full name including any prefix and/or suffix.
	 */
	public String DLName;
	/**
	 * @brief Drivers License: last name
	 * @details Family name of the cardholder. (Family name is sometimes also
	 *          called 'last name' or 'surname.') Collect full name for record,
	 *          print as many characters as possible on portrait side of DL/ID.
	 */
	public String DLNameLast;
	/**
	 * @brief Drivers License: first name
	 * @details First name of the cardholder.
	 */
	public String DLNameFirst;
	/**
	 * @brief Drivers License: middle name
	 * @details Middle name(s) of the cardholder. In the case of multiple middle
	 *          names they shall be separated by a comma ','.
	 */
	public String DLNameMid;
	/**
	 * @brief Drivers License: name suffix
	 * @details Name Suffix (If jurisdiction participates in systems requiring
	 *          name suffix (PDPS, CDLIS, etc.), the suffix must be collected
	 *          and displayed on the DL/ID and in the MRT). Collect full name
	 *          for record, print as many characters as possible on portrait
	 *          side of DL/ID. JR (Junior), SR (Senior), 1ST or I (First), 2ND
	 *          or II (Second), 3RD or III (Third), 4TH or IV (Fourth), 5TH or V
	 *          (Fifth), 6TH or VI (Sixth), 7TH or VII (Seventh), 8TH or VIII
	 *          (Eighth), 9TH or IX (Ninth).
	 */
	public String DLNameSfx;
	/**
	 * @brief Drivers License: name prefix
	 * @details Name prefix for the cardholder (e.g. Mr, Dr, etc.).
	 */
	public String DLNamePrfx;
	/**
	 * @brief Drivers License: mail address 1
	 * @details Street portion of the cardholder address.
	 */
	public String DLMailAddress1;
	/**
	 * @brief Drivers License: mail address 2
	 * @details Second line of street portion of the cardholder address.
	 */
	public String DLMailAddress2;
	/**
	 * @brief Drivers License: mail city
	 * @details City portion of the cardholder address.
	 */
	public String DLMailCity;
	/**
	 * @brief Drivers License: mail state
	 * @details State portion of the cardholder address.
	 */
	public String DLMailState;
	/**
	 * @brief Drivers License: mail postal code
	 * @details Postal code portion of the cardholder address in the U.S. and
	 *          Canada. If the trailing portion of the postal code in the U.S.
	 *          is not known, zeros will be used to fill the trailing set of
	 *          numbers up to nine (9) digits.
	 */
	public String DLMailPostalCode;

	/**
	 * @brief Drivers License: address 1
	 * @details Street portion of the cardholder residence address.
	 */
	public String DLAddress1;
	/**
	 * @brief Drivers License: address 2
	 * @details Second line of street portion of the cardholder residence
	 *          address.
	 */
	public String DLAddress2;
	/**
	 * @brief Drivers License: address 3
	 * @details
	 */
	public String DLAddress3; // Mag stripe only
	/**
	 * @brief Drivers License: city
	 * @details City portion of the cardholder residence address.
	 */
	public String DLCity;
	/**
	 * @brief Drivers License: state
	 * @details State portion of the cardholder residence address.
	 */
	public String DLState;
	/**
	 * @brief Drivers License: postal code
	 * @details Postal code portion of the cardholder residence address in the
	 *          U.S. and Canada. If the trailing portion of the postal code in
	 *          the U.S. is not known, zeros will be used to fill the trailing
	 *          set of numbers up to nine (9) digits.
	 */
	public String DLPostalCode;

	/**
	 * @brief Drivers License: ID number
	 * @details
	 */
	public String DLIDNumber;
	/**
	 * @brief Drivers License: ID number extension (mag stripe only)
	 * @details
	 */
	public String DLIDNumberExt;
	/**
	 * @brief Drivers License: class
	 * @details Jurisdiction-specific vehicle class / group code, designating
	 *          the type of vehicle the cardholder has privilege to drive.
	 */
	public String DLClass;
	/**
	 * @brief Drivers License: restrictions
	 * @details Jurisdiction-specific codes that represent restrictions to
	 *          driving privileges (such as airbrakes, automatic transmission,
	 *          daylight only, etc.).
	 */
	public String DLRestrictions;
	/**
	 * @brief Drivers License: endorsements
	 * @details Jurisdiction-specific codes that represent additional privileges
	 *          granted to the cardholder beyond the vehicle class (such as
	 *          transportation of passengers, hazardous materials, operation of
	 *          motorcycles, etc.).
	 */
	public String DLEndorsements;
	/**
	 * @brief Drivers License: height
	 * @details Height of cardholder. Inches (in): number of inches followed by
	 *          ' in' ex. 6'1'' = '073 in' Centimeters (cm): number of
	 *          centimeters followed by ' cm' ex. 181 centimeters='181 cm'.
	 */
	public String DLHeight;
	/**
	 * @brief Drivers License: height in centimeters
	 * @details Height of cardholder in centimeters.
	 */
	public String DLHeightCM;
	/**
	 * @brief Drivers License: weight
	 * @details Cardholder weight in pounds Ex. 185 lb = '185'.
	 */
	public String DLWeight;
	/**
	 * @brief Drivers License: weight in kilograms
	 * @details Cardholder weight in kilograms Ex. 84 kg = '084'.
	 */
	public String DLWeightKG;
	/**
	 * @brief Drivers License: eye color
	 * @details Color Color of cardholder's eyes. (ANSI D-20 codes).
	 */
	public String DLEyes;
	/**
	 * @brief Drivers License: hair color
	 * @details Bald, black, blonde, brown, gray, red/auburn, sandy, white,
	 *          unknown. If the issuing jurisdiction wishes to abbreviate
	 *          colors, the three-character codes provided in ANSI D20 must be
	 *          used.
	 */
	public String DLHair;

	/**
	 * @brief Drivers License: expiration date
	 * @details Date on which the driving and identification privileges granted
	 *          by the document are no longer valid. (MMDDCCYY for U.S.,
	 *          CCYYMMDD for Canada).
	 */
	public String DLExpires;
	/**
	 * @brief Drivers License: date of birth
	 * @details Date on which the cardholder was born. (MMDDCCYY for U.S.,
	 *          CCYYMMDD for Canada).
	 */
	public String DLBirthDate;
	/**
	 * @brief Drivers License: sex
	 * @details Gender of the cardholder. 1 = male, 2 = female.
	 */
	public String DLSex;
	/**
	 * @brief Drivers License: date of issue
	 * @details Date on which the document was issued. (MMDDCCYY for U.S.,
	 *          CCYYMMDD for Canada).
	 */
	public String DLIssueDate;
	/**
	 * @brief Drivers License: Social Security Number
	 * @details Cardholder's Social Security Number
	 */
	public String DLSSN;

	/**
	 * @brief Drivers License: template version (mag stripe only)
	 * @details
	 */
	public String DLTemplateVer;
	/**
	 * @brief Drivers License: security version (mag stripe only)
	 * @details
	 */
	public String DLSecurityVer;
	/**
	 * @brief Drivers License: reserver 1 (mag stripe only)
	 * @details
	 */
	public String DLReserved1;
	/**
	 * @brief Drivers License: reserved 2 (mag stripe only)
	 * @details
	 */
	public String DLReserved2;
	/**
	 * @brief Drivers License: security (mag stripe only)
	 * @details
	 */
	public String DLSecurity;

	/**
	 * @brief Permit: class
	 * @details Jurisdiction-specific vehicle class / group code, designating
	 *          the type of vehicle the cardholder has privilege to drive.
	 */
	public String PermitClass;
	/**
	 * @brief Permit: expiration date
	 * @details Date on which the driving and identification privileges granted
	 *          by the document are no longer valid. (MMDDCCYY for U.S.,
	 *          CCYYMMDD for Canada).
	 */
	public String PermitExpDate;
	/**
	 * @brief Permit: ID
	 * @details The number assigned or calculated by the issuing authority.
	 */
	public String PermitID;
	/**
	 * @brief Permit: issue date
	 * @details Date on which the document was issued. (MMDDCCYY for U.S.,
	 *          CCYYMMDD for Canada).
	 */
	public String PermitIssDate;
	/**
	 * @brief Permit: restrictions
	 * @details Jurisdiction-specific codes that represent restrictions to
	 *          driving privileges (such as airbrakes, automatic transmission,
	 *          daylight only, etc.).
	 */
	public String PermitRestrict;
	/**
	 * @brief Permit: endorsements
	 * @details Jurisdiction-specific codes that represent additional privileges
	 *          granted to the cardholder beyond the vehicle class (such as
	 *          transportation of passengers, hazardous materials, operation of
	 *          motorcycles, etc.).
	 */
	public String PermitEndorse;

	/**
	 * @brief Also Known as: SSN
	 * @details Other Social Security Number by which the cardholder has been
	 *          known.
	 */
	public String AKASSN;
	/**
	 * @brief Also Known as: name
	 * @details Other full name (including any prefix and/or suffix) by which
	 *          the cardholder has been known.
	 */
	public String AKAName;
	/**
	 * @brief Also Known as: last name
	 * @details Other last name(s) by which the cardholder has been known.
	 */
	public String AKANameLast;
	/**
	 * @brief Also Known as: first name
	 * @details Other first name(s) by which the cardholder has been known.
	 */
	public String AKANameFirst;
	/**
	 * @brief Also Known as: middle name
	 * @details Other middle name(s) by which the cardholder has been known. In
	 *          the case of multiple middle names they shall be separated by a
	 *          comma ','.
	 */
	public String AKANameMid;
	/**
	 * @brief Also Known as: name suffix
	 * @details
	 */
	public String AKANameSfx;
	/**
	 * @brief Also Known as: name prefix
	 * @details Other name prefix by whic the cardholder has been known (e.g.
	 *          Mr, Dr, etc.).
	 */
	public String AKANamePrfx;
	/**
	 * @brief Also Known as: birth date
	 * @details Other birth date which the cardholder has used.
	 */
	public String AKABirthDate;

	/**
	 * @brief Drivers License: Issue Timestamp
	 * @details
	 */
	public String DLIssTimestamp;
	/**
	 * @brief Drivers License: Number of Duplicates
	 * @details
	 */
	public String DLNumDuplicates;
	/**
	 * @brief Drivers License: Medical Indicator/Codes
	 * @details
	 */
	public String DLMedicalCodes;
	/**
	 * @brief Drivers License: organ donor
	 * @details Field that indicates that the cardholder is an organ donor =
	 *          '1'.
	 */
	public String DLOrganDonor;
	/**
	 * @brief Drivers License: Non-Resident Indicator
	 * @details
	 */
	public String DLNonResident;
	/**
	 * @brief Drivers License: Unique Customer Identifier
	 * @details
	 */
	public String DLCustomerID;

	/**
	 * @brief Drivers License: weight range
	 */
	public String DLWeightRange;
	/**
	 * @brief Drivers License: Document Discriminator
	 * @details Number must uniquely identify a particular document issued to
	 *          that customer from others that may have been issued in the past.
	 *          This number may serve multiple purposes of document
	 *          discrimination, audit information number, and/or inventory
	 *          control.
	 */
	public String DLDocumentDiscr;
	/**
	 * @brief Drivers License: country
	 * @details
	 */
	public String DLCountry;
	/**
	 * @brief Drivers License: Federal Commercial Codes
	 * @details
	 */
	public String DLFedCommCodes;
	/**
	 * @brief Drivers License: place of birth
	 * @details Country and municipality and/or state/province.
	 */
	public String DLPlaceOfBirth;
	/**
	 * @brief Drivers License: audit info
	 * @details A string of letters and/or numbers that identifies when, where,
	 *          and by whom a driver license/ID card was made. If audit
	 *          information is not used on the card or the MRT, it must be
	 *          included in the driver record.
	 */
	public String DLAuditInfo;
	/**
	 * @brief Drivers License: Inventory control number
	 * @details A string of letters and/or numbers that is affixed to the raw
	 *          materials (card stock, laminate, etc.) used in producing driver
	 *          licenses and ID cards. (DHS recommended field).
	 */
	public String DLInventoryCtrl;
	/**
	 * @brief Drivers License: race ethnicity
	 * @details
	 */
	public String DLRaceEthnicity;
	/**
	 * @brief Drivers License: Std Vehicle Class
	 * @details
	 */
	public String DLStdVehicleClass;
	/**
	 * @brief Drivers License: Std endorsements
	 * @details
	 */
	public String DLStdEndorsements;
	/**
	 * @brief Drivers License: Std restrictions
	 * @details
	 */
	public String DLStdRestrictions;
	/**
	 * @brief Drivers License: class description
	 * @details
	 */
	public String DLClassDesc;
	/**
	 * @brief Drivers License: endorsements description
	 * @details
	 */
	public String DLEndorsementsDesc;
	/**
	 * @brief Drivers License: restriction description
	 * @details
	 */
	public String DLRestrictionsDesc;

	/**
	 * @brief Height in Inches
	 * @details
	 */
	public String XHeightInches;
	/**
	 * @brief Height in Centimeters
	 * @details
	 */
	public String XHeightCentimeters;

	/**
	 * @brief Drivers License: Compliance Type
	 * @details
	 */
	public String DLComplianceType;
	/**
	 * @brief Drivers License: Revision Date
	 * @details
	 */
	public String DLRevisionDate;
	/**
	 * @brief Drivers License: HAZMAT Expiration Date
	 * @details
	 */
	public String DLHAZMATExpDate;
	/**
	 * @brief Drivers License: Limited Duration
	 * @details
	 */
	public String DLLimitedDuration;
	/**
	 * @brief Drivers License: Trunc Last
	 * @details
	 */
	public String DLNameTruncLast;
	/**
	 * @brief Drivers License: Trunc First
	 * @details
	 */
	public String DLNameTruncFirst;
	/**
	 * @brief Drivers License: Trunc Middle
	 * @details
	 */
	public String DLNameTruncMid;
	/**
	 * @brief Drivers License: Under 18 Until
	 * @details Date on which the cardholder turns 18 years old. (MMDDCCYY for
	 *          U.S., CCYYMMDD for Canada).
	 */
	public String DLUnder18Until;
	/**
	 * @brief Drivers License: Under 19 Until
	 * @details Date on which the cardholder turns 19 years old. (MMDDCCYY for
	 *          U.S., CCYYMMDD for Canada).
	 */
	public String DLUnder19Until;
	/**
	 * @brief Drivers License: Under 21 Until
	 * @details Date on which the cardholder turns 21 years old. (MMDDCCYY for
	 *          U.S., CCYYMMDD for Canada).
	 */
	public String DLUnder21Until;
	/**
	 * @brief Drivers License: Veteran
	 * @details Field that indicates that the cardholder is a veteran = '1'.
	 */
	public String DLVeteran;

	/**
	 * @brief Registration: Issue date
	 * @details
	 */
	public String RGIssueDate;
	/**
	 * @brief Registration: Expires
	 * @details
	 */
	public String RGExpires;
	/**
	 * @brief Registration: plate number
	 * @details
	 */
	public String RGPlateNum;
	/**
	 * @brief Registration: last name
	 * @details
	 */
	public String RGNameLast;
	/**
	 * @brief Registration: first name
	 * @details
	 */
	public String RGNameFirst;
	/**
	 * @brief Registration: Name
	 * @details
	 */
	public String RGName;
	/**
	 * @brief Registration: Middle Name
	 * @details
	 */
	public String RGNameMid;
	/**
	 * @brief Registration: Name Suffix
	 * @details
	 */
	public String RGNameSfx;
	/**
	 * @brief Registration: address
	 * @details
	 */
	public String RGAddress;
	/**
	 * @brief Registration: city
	 * @details
	 */
	public String RGCity;
	/**
	 * @brief Registration: postal code
	 * @details
	 */
	public String RGPostalCode;
	/**
	 * @brief Registration: Vehicle Identification Number
	 * @details
	 */
	public String RGVIN;
	/**
	 * @brief Registration: Make
	 * @details
	 */
	public String RGMake;
	/**
	 * @brief Registration: model year
	 * @details
	 */
	public String RGModelYear;
	/**
	 * @brief Registration: body
	 * @details
	 */
	public String RGBody;
	/**
	 * @brief Registration: year
	 * @details
	 */
	public String RGRegYear;
	/**
	 * @brief Registration: decal num
	 * @details
	 */
	public String RGDecalNum;

	/**
	 * @brief Registration: vehicle use
	 * @details
	 */
	public String RGVehicleUse;
	/**
	 * @brief Registration: fuel
	 * @details
	 */
	public String RGFuel;
	/**
	 * @brief Registration: axles
	 * @details
	 */
	public String RGAxles;
	/**
	 * @brief Registration: weight
	 * @details
	 */
	public String RGWeight;
	/**
	 * @brief Registration: model
	 * @details
	 */
	public String RGModel;
	/**
	 * @brief Registration: business name
	 * @details
	 */
	public String RGBusinessName;
	/**
	 * @brief Registration: color
	 * @details
	 */
	public String RGColor;

	/**
	 * @brief Insurrance Card: unit
	 * @details
	 */
	public String IRUnit;
	/**
	 * @brief Insurrance Card: vehicle type
	 * @details
	 */
	public String IRVehicleType;
	/**
	 * @brief Insurrance Card: seats
	 * @details
	 */
	public String IRSeats;
	/**
	 * @brief Insurrance Card: reg issue date
	 * @details
	 */
	public String IRRegIssueDate;
	/**
	 * @brief Insurrance Card: decal num
	 * @details
	 */
	public String IRDecalNum;
	/**
	 * @brief Insurrance Card: enforce date
	 * @details
	 */
	public String IREnforceDate;
	/**
	 * @brief Insurrance Card: base reg weight
	 * @details
	 */
	public String IRBaseRegWeight;
	/**
	 * @brief Insurrance Card: Insurance Company Code
	 * @details
	 */
	public String IRInsCompanyCode;
	/**
	 * @brief Insurrance Card: IR Effective Date
	 * @details
	 */
	public String IREffectiveDate;

	/**
	 * @brief Num Fields
	 * @details
	 */
	public String NumFields;
	/**
	 * @brief Parser Version
	 * @details
	 */
	public String XVersion;

	@Override
	public String toString() {
		if(DLIIN == null)
			return super.toString();
		Field[] fields = getClass().getFields();
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getName() + ":[\n");
		for (Field field : fields) {
			try {
				if(!field.get(this).toString().isEmpty()){
					sb.append("    ");
					sb.append(field.getName() + " = ");
					sb.append(field.get(this));
					sb.append("\n");
				}
			} catch (IllegalArgumentException e) {
				//e.printStackTrace();
				Log.i(TAG, "IllegalAccessException");
			} catch (IllegalAccessException e) {
			//	e.printStackTrace();
				Log.i(TAG, "IllegalAccessException");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
