package me.ellie.demobootrestapi.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() {
        // Given
        String password = "1234";
        String username = "test@test.com";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(account);

        // When
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
//        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(this.passwordEncoder.matches(password, userDetails.getPassword()));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void findByUsernameFail1() {
        String username = "random@email.com";
        accountService.loadUserByUsername(username);
    }

    @Test
    public void findByUsernameFail2() {
        String username = "random@email.com";

        try{
            accountService.loadUserByUsername(username);
            fail("supposed to be failed");
        }catch (UsernameNotFoundException e) {
            assertThat(e.getMessage()).containsSequence(username);
        }
    }

    @Test
    public void findByUsernameFail3() {
        // Expected
        String username = "random@email.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        // When
        accountService.loadUserByUsername(username);
    }

}