package security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import entity.MyUser;
import enums.Role;
import lombok.NoArgsConstructor;
import repository.UserRepository;

@Service
@NoArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		MyUser myUser = userRepository.findByEmail(email);
		if (myUser == null) {
			throw new UsernameNotFoundException(email);
		}
		return new CustomUserDetails(myUser.getId(), myUser.getEmail(), myUser.getPassword(),
				myUser.getFirstName() + ' ' + myUser.getName(), convertAuthorities(myUser.getRole()));
	}

	public String getUserFullname(String email) {
		MyUser user = userRepository.findByEmail(email);
		return user.getFullName();
	}

	private Collection<? extends GrantedAuthority> convertAuthorities(Role role) {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toString()));
	}
}
