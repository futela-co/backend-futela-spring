package com.futela.api.application.usecase.listing;

import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.domain.exception.DuplicateResourceException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.property.Listing;
import com.futela.api.domain.port.out.property.ListingRepositoryPort;
import com.futela.api.domain.port.out.property.PropertyRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ListingUseCaseService {

    private final ListingRepositoryPort listingRepository;
    private final PropertyRepositoryPort propertyRepository;

    public ListingUseCaseService(ListingRepositoryPort listingRepository,
                                 PropertyRepositoryPort propertyRepository) {
        this.listingRepository = listingRepository;
        this.propertyRepository = propertyRepository;
    }

    public void addToFavorites(UUID userId, UUID propertyId, UUID companyId) {
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", propertyId.toString()));

        if (listingRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new DuplicateResourceException("Cette propriété est déjà dans vos favoris");
        }

        var listing = new Listing(null, userId, propertyId, companyId, null);
        listingRepository.save(listing);
    }

    public void removeFromFavorites(UUID userId, UUID propertyId) {
        if (!listingRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new ResourceNotFoundException("Favori", propertyId.toString());
        }
        listingRepository.deleteByUserIdAndPropertyId(userId, propertyId);
    }

    @Transactional(readOnly = true)
    public List<PropertySummaryResponse> getUserFavorites(UUID userId) {
        return listingRepository.findByUserId(userId).stream()
                .map(listing -> propertyRepository.findById(listing.propertyId()))
                .filter(java.util.Optional::isPresent)
                .map(opt -> PropertySummaryResponse.fromDomain(opt.get()))
                .toList();
    }
}
