package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.service.ServiceDetailDTO;
import com.erick.nutricontrol.dto.service.ServiceRequestDTO;
import com.erick.nutricontrol.dto.service.ServiceUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceService {
    ServiceDetailDTO addService(ServiceRequestDTO dto);
    Page<ServiceDetailDTO> listServices(Pageable pageable);
    ServiceDetailDTO getServiceById(Long id);
    ServiceDetailDTO updateService(Long id, ServiceUpdateDTO dto);
    void deleteService(Long id);
}
