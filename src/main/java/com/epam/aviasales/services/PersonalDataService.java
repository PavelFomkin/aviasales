package com.epam.aviasales.services;

import com.epam.aviasales.domain.PersonalData;
import com.epam.aviasales.exceptions.PersonalDataAlreadyExists;

import java.util.List;

public interface PersonalDataService {

  List<PersonalData> getPersonalDatas();

  List<PersonalData> getPersonalDatas(int page, int count);

  PersonalData getPersonalDataByPassport(String passport);

  PersonalData getPersonalDataById(Long id);

  void addPersonalData(PersonalData personalData) throws PersonalDataAlreadyExists;

  boolean isExist(PersonalData personalData);
}
