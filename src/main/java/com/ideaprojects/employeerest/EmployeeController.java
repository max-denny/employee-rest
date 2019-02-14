package com.ideaprojects.employeerest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
class EmployeeController {

    private final EmployeeRepository repository;

    EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value="/employees", produces={"application/json"})
    // @GetMapping("/employees")
    Resources<Resource<Employee>> all() {

        List<Resource<Employee>> employees = repository.findAll().stream()
            .map(employee -> new Resource<>(employee,
            linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
            linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
            .collect(Collectors.toList());

            return new Resources<>(employees,
            linkTo(methodOn(EmployeeController.class).all()).withSelfRel());

    }
    

    @PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee newEmployee){
        return repository.save(newEmployee);
    }

    // @GetMapping("/employees/{id}")
	// Employee one(@PathVariable Long id) {

	// 	return repository.findById(id)
	// 		.orElseThrow(() -> new EmployeeNotFoundException(id));
    // }
    
    @GetMapping(value="/employees/{id}", produces={"application/hal+json"})
Resource<Employee> one(@PathVariable Long id) {

	Employee employee = repository.findById(id)
		.orElseThrow(() -> new EmployeeNotFoundException(id));

	return new Resource<>(employee,
		linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
		linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
}

    // @GetMapping("/employees/{id}")
    // Resource<Employee> one(@PathVariable Long id) {

    //     Employee employee = repository.findById(id)
    //       .orElseThrow(() -> new EmployeeNotFoundException(id));

    //       return new Resource<>(employee,
    //       linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
    //       linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
    // }

    @PutMapping("/employees/{id}")
    Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id){

        return repository.findById(id)
            .map(employee -> {
                employee.setName(newEmployee.getName());
                employee.setRole(newEmployee.getRole());
                return repository.save(employee);
            })
            .orElseGet(() -> {
                newEmployee.setId(id);
                return repository.save(newEmployee);
            });
    }

    @DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }


    }
