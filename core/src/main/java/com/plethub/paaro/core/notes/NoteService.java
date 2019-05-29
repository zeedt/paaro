package com.plethub.paaro.core.notes;

import com.plethub.paaro.core.models.Notes;
import com.plethub.paaro.core.notes.dto.NoteDTO;
import com.plethub.paaro.core.notes.repository.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService {

    @Autowired
    NotesRepository notesRepository;

    public Page<NoteDTO> getCustomerNotes(Long id, Pageable pageDetails) {
        Page<Notes> page = notesRepository.findAllByCustomer_Id(id, pageDetails);
        List<NoteDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<NoteDTO> pageImpl = new PageImpl<NoteDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    public String createNote(NoteDTO noteDTO){

        Notes notes = new Notes();
        notes.setAdmin(noteDTO.getAdmin());
        notes.setCustomer(noteDTO.getCustomer());
        notes.setMadeOn(noteDTO.getMadeOn());
        notes.setComment(noteDTO.getComment());
        notesRepository.save(notes);
        return "Comment Successfully added";

    }

    public NoteDTO getNote(Long id) {
        Notes subscription = this.notesRepository.getOne(id);
        return convertEntityToDTO(subscription);
    }


    public List<NoteDTO> convertEntitiesToDTOs(List<Notes> subscriptions) {
        List<NoteDTO> subscriptionDTOList = new ArrayList<>();
        for (Notes subscription : subscriptions) {
            NoteDTO subscriptionDTO = convertEntityToDTO(subscription);
            subscriptionDTOList.add(subscriptionDTO);
        }
        return subscriptionDTOList;
    }

    public NoteDTO convertEntityToDTO(Notes notes) {
        NoteDTO noteDTO = new NoteDTO();
        noteDTO.setId(notes.getId());
        noteDTO.setCustomer(notes.getCustomer());
        noteDTO.setComment(notes.getComment());
        noteDTO.setMadeOn(notes.getMadeOn());
        noteDTO.setAdmin(notes.getAdmin());
        return noteDTO;
    }
}
