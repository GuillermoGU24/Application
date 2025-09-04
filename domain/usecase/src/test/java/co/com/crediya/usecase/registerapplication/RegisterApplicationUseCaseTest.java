package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.LoanType;
import co.com.crediya.model.application.State;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.IdentityGateway;
import co.com.crediya.model.application.gateways.LoanTypeRepository;
import co.com.crediya.model.application.gateways.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RegisterApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private IdentityGateway identityGateway;

    @InjectMocks
    private RegisterApplicationUseCase useCase;

    private Application app;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        app = Application.builder()
                .applicationId(1L)
                .document("12345")
                .amount(3000.0)
                .term(12)
                .loanTypeId(10L)
                .build();
    }

    @Test
    void shouldFailWhenClientDoesNotExist() {
        when(identityGateway.existsByDocument("12345")).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.create(app))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().equals("document: Client does not exist"))
                .verify();
    }

    @Test
    void shouldFailWhenLoanTypeNotFound() {
        when(identityGateway.existsByDocument("12345")).thenReturn(Mono.just(true));
        when(loanTypeRepository.findById(10L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.create(app))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().equals("loanType: Loan type not found"))
                .verify();
    }

    @Test
    void shouldFailWhenAmountOutOfRange() {
        app.setAmount(10000.0); // fuera del rango
        when(identityGateway.existsByDocument("12345")).thenReturn(Mono.just(true));
        when(loanTypeRepository.findById(10L)).thenReturn(Mono.just(
                LoanType.builder().loanTypeId(10L).minAmount(1000.0).maxAmount(5000.0).build()
        ));

        StepVerifier.create(useCase.create(app))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().equals("amount: Amount out of allowed range"))
                .verify();
    }

    @Test
    void shouldFailWhenStateNotFound() {
        when(identityGateway.existsByDocument("12345")).thenReturn(Mono.just(true));
        when(loanTypeRepository.findById(10L)).thenReturn(Mono.just(
                LoanType.builder().loanTypeId(10L).minAmount(1000.0).maxAmount(5000.0).build()
        ));
        when(stateRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.create(app))
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().equals("state: Initial state not found"))
                .verify();
    }

    @Test
    void shouldCreateApplicationSuccessfully() {
        LoanType loanType = LoanType.builder()
                .loanTypeId(10L)
                .minAmount(1000.0)
                .maxAmount(5000.0)
                .build();

        State state = State.builder()
                .stateId(1L)
                .name("Pendiente de revisión")
                .description("Solicitud registrada")
                .build();

        when(identityGateway.existsByDocument("12345")).thenReturn(Mono.just(true));
        when(loanTypeRepository.findById(10L)).thenReturn(Mono.just(loanType));
        when(stateRepository.findById(1L)).thenReturn(Mono.just(state));
        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.create(app))
                .expectNextMatches(savedApp ->
                        savedApp.getStateId().equals(1L) &&
                                savedApp.getAmount().equals(3000.0) &&
                                savedApp.getDocument().equals("12345"))
                .verifyComplete();
    }
}
