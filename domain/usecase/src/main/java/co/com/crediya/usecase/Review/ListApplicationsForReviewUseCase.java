package co.com.crediya.usecase.Review;

import co.com.crediya.model.application.ApplicationForReview;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.IdentityGateway;
import co.com.crediya.model.application.gateways.UserGateway;
import co.com.crediya.model.auth.User;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ListApplicationsForReviewUseCase {

    private final ApplicationRepository repository;
    private final IdentityGateway identityGateway;
    private final UserGateway userGateway; // nuevo

    public Mono<PagedResult<ApplicationForReview>> listForUser(
            String bearerToken, List<String> estados, int page, int size) {

        return identityGateway.findAuthUserByToken(bearerToken)
                .flatMap(authUser -> {
                    if ("CLIENTE".equalsIgnoreCase(authUser.getRol().getName())) {
                        return Mono.error(new IllegalArgumentException("forbidden: Requires ASESOR role"));
                    }

                    return repository.findForReview(estados, page, size).collectList()
                            .zipWith(repository.countForReview(estados))
                            .flatMap(tuple -> {
                                List<ApplicationForReview> apps = tuple.getT1();
                                long total = tuple.getT2();

                                List<String> documents = apps.stream().map(ApplicationForReview::getDocument).toList();

                                return userGateway.findUsersByDocuments(documents, bearerToken)
                                        .collectMap(User::getDocument)
                                        .map(usersMap -> {
                                            apps.forEach(app -> {
                                                User user = usersMap.get(app.getDocument());
                                                if (user != null) {
                                                    app.setFullName(user.getName() + " " + user.getLastName());
                                                    app.setEmail(user.getEmail());
                                                    app.setBaseSalary(user.getBaseSalary() != null ? user.getBaseSalary().longValue() : null);
                                                }
                                                if (app.getAmount() != null && app.getTerm() != null && app.getTerm() > 0) {
                                                    double monthly = app.getAmount() / app.getTerm();
                                                    app.setMonthlyApplicationAmount(monthly);
                                                }
                                            });
                                            return new PagedResult<>(apps, total, page, size);
                                        });
                            });


                });
    }

    public record PagedResult<T>(List<T> items, long total, int page, int size) {}
}
