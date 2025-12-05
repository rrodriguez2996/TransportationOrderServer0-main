
package es.upm.dit.apsv.transportationorderserver.repository;


import org.springframework.data.repository.CrudRepository;

import es.upm.dit.apsv.transportationorderserver.model.TransportationOrder;


public interface TransportationOrderRepository extends                   
    CrudRepository<TransportationOrder,String> {

    TransportationOrder findByTruckAndSt(String truck, int st);

}