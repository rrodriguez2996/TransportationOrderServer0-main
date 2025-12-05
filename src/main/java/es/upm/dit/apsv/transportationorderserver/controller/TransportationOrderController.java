package es.upm.dit.apsv.transportationorderserver.controller;
import java.util.List;

import java.util.Optional;


import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;


import es.upm.dit.apsv.transportationorderserver.repository.TransportationOrderRepository;

import es.upm.dit.apsv.transportationorderserver.model.TransportationOrder;


@RestController

public class TransportationOrderController {

    private final TransportationOrderRepository repository;


    public static final Logger log = LoggerFactory.getLogger(TransportationOrderController.class);


    public TransportationOrderController(TransportationOrderRepository repository) {

      this.repository = repository;

    }

 

    @GetMapping("/transportationorders")

    List<TransportationOrder> all() {

      return (List<TransportationOrder>) repository.findAll();

    }


    @PostMapping("/transportationorders")

    TransportationOrder newOrder(@RequestBody TransportationOrder newOrder) {

      return repository.save(newOrder);

    }

   

    @GetMapping("/transportationorders/{truck}")

    ResponseEntity<TransportationOrder> getByTruck(@PathVariable String truck) {

      Optional<TransportationOrder> ot = repository.findById(truck);

      if (ot.isPresent())

        return new ResponseEntity<>(ot.get(), HttpStatus.OK);

      return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

 

    @PutMapping("/transportationorders")

    ResponseEntity<TransportationOrder> update(@RequestBody TransportationOrder updatedOrder) {

      TransportationOrder to = repository.save(updatedOrder);

      if (to == null)

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

      return new ResponseEntity<>(to, HttpStatus.OK);

    }

 

    @DeleteMapping("/transportationorders/{truck}")

    void deleteOrder(@PathVariable String truck) {

      repository.deleteById(truck);

    }    

}