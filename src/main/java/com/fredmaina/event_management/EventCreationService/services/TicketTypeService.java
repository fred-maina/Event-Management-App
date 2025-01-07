package com.fredmaina.event_management.EventCreationService.services;


import com.fredmaina.event_management.EventCreationService.DTOs.TicketTypeDTO;
import com.fredmaina.event_management.EventCreationService.Models.Event;
import com.fredmaina.event_management.EventCreationService.Models.TicketType;
import com.fredmaina.event_management.EventCreationService.repositories.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Optional<TicketType> updateTicketType(UUID id, TicketTypeDTO ticketTypeDTO){
        Optional<TicketType> ticketType=ticketTypeRepository.findById(id);
        if (ticketType.isEmpty()){
            return Optional.empty();
        }
        if (ticketTypeDTO.getNumberOfTickets() != 0){
            ticketType.get().setNumberOfTickets(ticketTypeDTO.getNumberOfTickets());
        }
        if (ticketTypeDTO.getTypeCategory() != null){
            ticketType.get().setTypeCategory(ticketTypeDTO.getTypeCategory());
        }
        if (ticketTypeDTO.getPrice() !=0){
            ticketType.get().setPrice(ticketTypeDTO.getPrice());
        }
         return Optional.of(ticketTypeRepository.save(ticketType.get()));

    }


}
