package com.fredmaina.event_management.services;


import com.fredmaina.event_management.models.Event;
import com.fredmaina.event_management.models.TicketType;
import com.fredmaina.event_management.repositories.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketTypeService {
    @Autowired
    TicketTypeRepository ticketTypeRepository;
    public void createTicketType(List<TicketType> ticketTypes){
        ticketTypeRepository.saveAll(ticketTypes);
    }
    public List<TicketType>  findAllTicketTypesByEvent(Event event){
        return ticketTypeRepository.findAllByEvent(event);
    }


}
