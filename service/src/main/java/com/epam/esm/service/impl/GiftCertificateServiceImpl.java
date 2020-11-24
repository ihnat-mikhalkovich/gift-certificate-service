package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dto.CertificateSearchCriteria;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.GiftCertificateNotFoundException;
import com.epam.esm.mapper.GiftCertificateMapper;
import com.epam.esm.service.GiftCertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private final GiftCertificateDao giftCertificateDao;
    private final TagDao tagDao;
    private final GiftCertificateMapper giftCertificateMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GiftCertificateDto> findAll(int page, int size) {
        return giftCertificateDao.findAll(page, size).stream()
                .map(giftCertificateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCertificateDto> findAll(
            int page,
            int size,
            CertificateSearchCriteria searchCriteria
    ) {
        List<GiftCertificate> certificates;
        if (areAllParamsEqualsToNull(searchCriteria)) {
            certificates = giftCertificateDao.findAll(page, size);
        } else {
            certificates = giftCertificateDao.findAll(searchCriteria, page, size);
        }
        return certificates.stream()
                .map(giftCertificateMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean areAllParamsEqualsToNull(CertificateSearchCriteria criteria) {
        return StringUtils.isEmpty(criteria.getName()) &&
                StringUtils.isEmpty(criteria.getDescription()) &&
                StringUtils.isEmpty(criteria.getSortByName()) &&
                StringUtils.isEmpty(criteria.getSortByCreateDate()) &&
                criteria.getTags().isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public GiftCertificateDto findById(long id)
            throws GiftCertificateNotFoundException {
        GiftCertificate byId = giftCertificateDao.findById(id)
                .orElseThrow(() -> new GiftCertificateNotFoundException(id));
        return giftCertificateMapper.toDto(byId);
    }

    @Override
    @Transactional
    public GiftCertificateDto create(GiftCertificateDto certificateDto) {
        GiftCertificate giftCertificate = giftCertificateMapper.toModel(certificateDto);
        Set<Tag> tags = fillCertificateTags(giftCertificate);
        giftCertificate.setTags(tags);
        giftCertificate.setCreateDate(ZonedDateTime.now());
        GiftCertificate createdCertificate = giftCertificateDao.save(giftCertificate);
        return giftCertificateMapper.toDto(createdCertificate);
    }

    @Override
    @Transactional
    public GiftCertificateDto update(GiftCertificateDto certificateDto)
            throws GiftCertificateNotFoundException {
        boolean isCertificateExist = giftCertificateDao.findById(certificateDto.getId()).isPresent();
        if (!isCertificateExist) {
            throw new GiftCertificateNotFoundException(certificateDto.getId());
        }
        GiftCertificate giftCertificate = giftCertificateMapper.toModel(certificateDto);
        Set<Tag> tags = fillCertificateTags(giftCertificate);
        giftCertificate.setTags(tags);
        giftCertificate.setLastUpdateDate(ZonedDateTime.now());
        giftCertificateDao.update(giftCertificate);
        log.debug("Updated certificate {}", giftCertificate);
        return giftCertificateMapper.toDto(giftCertificate);
    }

    private Set<Tag> fillCertificateTags(GiftCertificate certificate) {
        Set<Tag> tags = new HashSet<>();
        certificate.getTags().forEach(tag -> {
            Optional<Tag> existedTag = tagDao.findByName(tag.getName());
            if (existedTag.isPresent()) {
                tags.add(existedTag.get());
            } else {
                tags.add(tag);
            }
        });
        log.debug("Certificate tags {}", tags);
        return tags;
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        giftCertificateDao.deleteById(id);
    }
}
