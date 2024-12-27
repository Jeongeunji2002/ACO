package org.myweb.first.security.jwt.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.myweb.first.security.jwt.jpa.entity.RefreshToken;
import org.myweb.first.security.jwt.jpa.repository.RefreshRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
//@RequiredArgsConstructor
public class RefreshService {
    private final RefreshRepository refreshRepository;

    public RefreshService(RefreshRepository refreshRepository) {
        this.refreshRepository = refreshRepository;
    }

    public void save(RefreshToken refreshToken) {
        refreshRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByTokenValue(String token) {
        return refreshRepository.findByTokenValue(token);
    }

    public Boolean exitsByRefreshToken(String refreshValue) {
        return refreshRepository.existsByTokenValue(refreshValue);
    }

    public void deleteByRefreshToken(String refreshValue) {
        refreshRepository.deleteByTokenValue(refreshValue);
    }

    public List<RefreshToken> findByUserId(String userId) {
        return refreshRepository.findByUserId(userId);
    }
}
