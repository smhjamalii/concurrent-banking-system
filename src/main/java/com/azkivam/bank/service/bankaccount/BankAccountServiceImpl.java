package com.azkivam.bank.service.bankaccount;

import com.azkivam.bank.dto.BankAccount;
import com.azkivam.bank.dto.mapper.BankAccountMapper;
import com.azkivam.bank.model.BankAccountModel;
import com.azkivam.bank.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor @Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Override
    public BankAccount save(BankAccount dto) {

        BankAccountModel model = BankAccountMapper.toModel.apply(dto);

        bankAccountRepository.save(model);

        return BankAccountMapper.toDTO.apply(model);
    }

    @Override
    public boolean delete(BankAccount dto) {
        try {

            bankAccountRepository.deleteById(dto.id());
            return true;
        } catch (Exception e) {

            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<BankAccount> findById(Long id) {

        Optional<BankAccountModel> model = bankAccountRepository.findById(id);

        if(model.isPresent()) {

            return model.map(BankAccountMapper.toDTO);
        } else {

            return Optional.empty();
        }
    }

    @Override
    public Optional<BankAccount> findByHolderUsername(String username) {

        return bankAccountRepository.findByHolderUsername(username).map(BankAccountMapper.toDTO);
    }

    @Override
    public Optional<BankAccount> findByNumber(String number) {

        Optional<BankAccountModel> model = bankAccountRepository.findByNumber(number);

        if(model.isPresent()) {

            return model.map(BankAccountMapper.toDTO);
        } else {

            return Optional.empty();
        }
    }

}
