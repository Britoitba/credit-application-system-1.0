package me.dio.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import org.apache.el.stream.Optional
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import java.util.UUID.randomUUID
import kotlin.collections.ArrayList
import kotlin.collections.List

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var customerService: CustomerService
    @MockK lateinit var creditRepository: CreditRepository
    @InjectMockKs lateinit var creditService: CreditService


    @Test
    fun shouldSaveCredit(){
        //given
        val fakeCustomer: Customer = buildCustomer()
        val fakeCredit: Credit = buildCredit(customer = fakeCustomer)
        every {
            creditRepository.save(any())
        } returns fakeCredit
        //when
        val actual: Credit = creditRepository.save(fakeCredit)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        Assertions.assertThat(actual.customer!!.firstName).isSameAs(fakeCredit.customer!!.firstName)
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun shouldFindCreditByCreditCode(){
        //given
        val fakeCreditCode: UUID = randomUUID()
        val fakeCustomer: Customer = buildCustomer()
        val fakeCredit: Credit = buildCredit(customer = fakeCustomer)
        every { creditRepository.findByCreditCode(fakeCreditCode) } returns fakeCredit
        //when
        val actual: Credit? = creditRepository.findByCreditCode(fakeCreditCode)
        //then
        Assertions.assertThat(actual?.customer?.id).isSameAs(fakeCredit.customer?.id)
    }

    fun buildCredit(
        creditValue: BigDecimal = BigDecimal.valueOf(1000.00),
        dayFirstOfInstallment: LocalDate = LocalDate.now().plusDays(3),
        numberOfInstallments: Int = 2,
        customer: Customer
    ): Credit = Credit (
        creditValue = creditValue,
        dayFirstInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstallments,
        customer = customer
    )

    private fun buildCustomer(
        firstName: String = "Francisco",
        lastName: String = "Nascimento",
        cpf: String = "11122233312",
        email: String = "teste@teste.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua do Teste",
        income: BigDecimal = BigDecimal.valueOf(1000.00),
        id: Long = 1L
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street
        ),
        income = income,
        id = id
    )

}