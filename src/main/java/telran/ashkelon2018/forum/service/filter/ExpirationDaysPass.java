package telran.ashkelon2018.forum.service.filter;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredential;
import telran.ashkelon2018.forum.dao.UserAccRepository;
import telran.ashkelon2018.forum.domain.UserAccount;

@Service
@Order(2)
public class ExpirationDaysPass implements Filter {

	@Autowired
	UserAccRepository repository;

	@Autowired
	AccountConfiguration configuration;

	@Override
	public void doFilter(ServletRequest rqst, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) rqst;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();

		String token = request.getHeader("Authorization");

		if (!path.startsWith("/account/password") || token != null) {

			AccountUserCredential userCredential = configuration.tokenDecode(token);
			UserAccount userAccount = repository.findById(userCredential.getLogin()).orElse(null);
			if (userAccount.getExpDate().isBefore(LocalDateTime.now())) {

				response.sendError(403, "PasswordExpire");
				return;

			}

		}

		chain.doFilter(request, response);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
