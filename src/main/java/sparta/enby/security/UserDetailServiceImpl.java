package sparta.enby.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sparta.enby.model.Account;
import sparta.enby.repository.AccountRepository;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Account account = accountRepository.findByNickname(username).orElseThrow(()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        return new UserDetailsImpl(account);
    }
}
