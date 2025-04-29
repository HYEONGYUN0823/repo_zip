package com.a7a7.modeule.member;

public class MemberDto {
	
	private String seq;
	private String name;
	private int gender;
	private String btd;
	private String iD;
	private String passWord;
	private String delNy;
	private String admSignin;
	private int mobileCarrier;
	private String phoneNumber;
	private String regDateTime;
	private String modDateTime;
	private String email;
	private String currentPassword; // 현재 비밀번호
	
	// 우편번호
	private String zipCode;
	private String roadNameAddress;
	private String Address;
	private String detailedAddress;
	private String referenceItem;
	private double longitude;
	private double latitude;
	

	private String xifcdSeqCount;	// 갯수
	
//	-----
	
	

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getBtd() {
		return btd;
	}

	public void setBtd(String btd) {
		this.btd = btd;
	}
	

	public String getiD() {
		return iD;
	}

	public void setiD(String iD) {
		this.iD = iD;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getDelNy() {
		return delNy;
	}

	public void setDelNy(String delNy) {
		this.delNy = delNy;
	}

	public String getAdmSignin() {
		return admSignin;
	}

	public void setAdmSignin(String admSignin) {
		this.admSignin = admSignin;
	}

	public String getXifcdSeqCount() {
		return xifcdSeqCount;
	}

	public void setXifcdSeqCount(String xifcdSeqCount) {
		this.xifcdSeqCount = xifcdSeqCount;
	}

	public int getMobileCarrier() {
		return mobileCarrier;
	}

	public void setMobileCarrier(int mobileCarrier) {
		this.mobileCarrier = mobileCarrier;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRegDateTime() {
		return regDateTime;
	}

	public void setRegDateTime(String regDateTime) {
		this.regDateTime = regDateTime;
	}

	public String getModDateTime() {
		return modDateTime;
	}

	public void setModDateTime(String modDateTime) {
		this.modDateTime = modDateTime;
	}
	
	
//	----- 우편번호 -----
	

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getRoadNameAddress() {
		return roadNameAddress;
	}

	public void setRoadNameAddress(String roadNameAddress) {
		this.roadNameAddress = roadNameAddress;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getDetailedAddress() {
		return detailedAddress;
	}

	public void setDetailedAddress(String detailedAddress) {
		this.detailedAddress = detailedAddress;
	}

	public String getReferenceItem() {
		return referenceItem;
	}

	public void setReferenceItem(String referenceItem) {
		this.referenceItem = referenceItem;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}	
	
	
	
}
