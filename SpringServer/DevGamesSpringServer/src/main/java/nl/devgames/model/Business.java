package nl.devgames.model;

import java.util.Set;

public class Business extends Model {

    private String name;

    Set<User> employees;
    Set<Project> projects;


    public Business() {
    }

    public Business(String name, Set<User> employees, Set<Project> projects) {
        this.name = name;
        this.employees = employees;
        this.projects = projects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<User> employees) {
        this.employees = employees;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "Business{" +
                "name='" + name + '\'' +
                ", employees=" + employees +
                ", projects=" + projects +
                '}';
    }


}
