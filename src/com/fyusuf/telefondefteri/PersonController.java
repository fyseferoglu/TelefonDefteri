package com.fyusuf.telefondefteri;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class PersonController {
	private List<Person> personList;
	private PersonDbUtil personDbUtil;
	private String search;
	private Logger logger = Logger.getLogger(getClass().getName());

	public PersonController() throws Exception {
		personList = new ArrayList<>();

		personDbUtil = PersonDbUtil.getInstance();
	}

	public List<Person> getPersonList() {
		return personList;
	}

	public String getSearch() {
		return search;
	}	

	public void setSearch(String search) {
		this.search = search;
	}

	public void loadPerson() {
		logger.info("Loading person list");

		personList.clear();

		try {
			personList = personDbUtil.getPersonList();
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error loading person list", exc);

			addErrorMessage(exc);
		}
	}

	public String addPerson(Person person) {
		logger.info("Adding person: " + person);

		try {
			personDbUtil.addPerson(person);
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error adding person", exc);

			addErrorMessage(exc);

			return null;
		}

		return "kayitlistesi?faces-redirect=true";
	}

	public String loadPerson(int personId) {
		logger.info("loading person: " + personId);

		try {
			Person person = personDbUtil.getPerson(personId);

			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.put("person", person);
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error loading person id:" + personId, exc);

			addErrorMessage(exc);

			return null;
		}

		return "kayitguncelle.xhtml";
	}

	public String updatePerson(Person person) {
		logger.info("updating person: " + person);

		try {
			personDbUtil.updatePerson(person);
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error updating person: " + person, exc);

			addErrorMessage(exc);

			return null;
		}

		return "kayitlistesi?faces-redirect=true";
	}

	public String deletePerson(int personId) {
		logger.info("Deleting person id: " + personId);

		try {
			personDbUtil.deletePerson(personId);
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error deleting person id: " + personId, exc);

			addErrorMessage(exc);

			return null;
		}

		return "kayitlistesi";
	}

	public String searchPerson(String key) {
		logger.info("Searching person");

		personList.clear();

		try {
			personList = personDbUtil.searchPerson(key);
			setSearch("");
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Error loading person list", exc);

			addErrorMessage(exc);
		}
		return "kayitara?faces-redirect=true";
	}

	private void addErrorMessage(Exception exc) {
		FacesMessage message = new FacesMessage("Error: " + exc.getMessage());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
