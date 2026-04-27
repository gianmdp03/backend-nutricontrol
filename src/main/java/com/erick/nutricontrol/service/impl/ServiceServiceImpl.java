package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.model.Service;
import com.erick.nutricontrol.dto.service.ServiceDetailDTO;
import com.erick.nutricontrol.dto.service.ServiceRequestDTO;
import com.erick.nutricontrol.dto.service.ServiceUpdateDTO;
import com.erick.nutricontrol.mapper.ServiceMapper;
import com.erick.nutricontrol.repository.ServiceRepository;
import com.erick.nutricontrol.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceServiceImpl implements ServiceService {
    private final ServiceRepository repository;
    private final ServiceMapper mapper;

    @Override
    @Transactional
    public ServiceDetailDTO addService(ServiceRequestDTO dto) {
         Service service = mapper.toEntity(dto);
         service = repository.save(service);
         return mapper.toDetailDTO(service);
    }

    @Override
    public Page<ServiceDetailDTO> listServices(Pageable pageable) {
        Page<Service> page = repository.findAll(pageable);
        if(page.isEmpty()){
            return Page.empty();
        }
        return page.map(mapper::toDetailDTO);
    }

    @Override
    public ServiceDetailDTO getServiceById(Long id) {
        Service service = repository.findById(id).orElseThrow(()-> new NotFoundException("Service not found"));
        return mapper.toDetailDTO(service);
    }

    @Override
    @Transactional
    public ServiceDetailDTO updateService(Long id, ServiceUpdateDTO dto) {
        Service service =  repository.findById(id).orElseThrow(()-> new NotFoundException("Service not found"));
        mapper.updateEntityFromDto(dto, service);
        service = repository.save(service);
        return mapper.toDetailDTO(service);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        Service service = repository.findById(id).orElseThrow(()-> new NotFoundException("Service not found"));
        repository.delete(service);
    }
}
