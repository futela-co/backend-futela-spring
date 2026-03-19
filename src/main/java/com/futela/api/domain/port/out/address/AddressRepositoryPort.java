package com.futela.api.domain.port.out.address;

import com.futela.api.domain.model.address.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepositoryPort {
    Optional<Address> findById(UUID id);
    Address save(Address address);
    List<Address> search(String query);
}
