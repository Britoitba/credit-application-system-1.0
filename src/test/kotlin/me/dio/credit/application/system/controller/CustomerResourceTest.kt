package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.dto.request.CustomerUpdateDto
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
    @Autowired
    private lateinit var customerRepository: CustomerRepository
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach fun setup() = customerRepository.deleteAll()
    @AfterEach fun tearDown() = customerRepository.deleteAll()

    @Test
    fun shouldCreateACustomerAndReturn201Status(){
        //given
        val customerDto: CustomerDto = buildCustomerDTO()
        val valueAsString = objectMapper.writeValueAsString(customerDto)
        //when
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Francisco"))
    }

    @Test
    fun shouldNotsaveACustomerWithSameCPFandReturn409Status(){
        customerRepository.save(buildCustomerDTO().toEntity())
        val customerDto: CustomerDto = buildCustomerDTO()
        val valueAsString = objectMapper.writeValueAsString(customerDto)

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isConflict)

    }

    @Test
    fun shouldNotsaveACustomerWithFirstNameEmptyandReturn400Status() {
        val customerDto: CustomerDto = buildCustomerDTO(firstName = "")
        val valueAsString = objectMapper.writeValueAsString(customerDto)

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun shouldFindCustomerByIdAndReturn200Status(){
        val customer: Customer = customerRepository.save(buildCustomerDTO().toEntity())

        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun shouldNotFindCustomerWithInvalidIdAndReturn400Status(){
        val invalidId: Long = 50L
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${invalidId}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun shouldDeleteCustomerById(){
        val customer: Customer = customerRepository.save(buildCustomerDTO().toEntity())

        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun shouldNotDeleteCustomerByInvalidIdAndReturn400Status(){
        val invalidId: Long = 50L
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${invalidId}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun shouldUpdateACustumerAndReturn200(){
        val customer: Customer = customerRepository.save(buildCustomerDTO().toEntity())
        val customerUpdateDto: CustomerUpdateDto = buildCustomerUpdateDTO()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)

        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("FranciscoUpdate"))
    }

    @Test
    fun shouldNotUpdateACustumerWithInvalidIdAndReturn400(){
        val invalidId: Long = 50L
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${invalidId}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    private fun buildCustomerDTO(
        firstName: String = "Francisco",
        lastName: String = "Nascimento",
        cpf: String = "10250788616",
        email: String = "teste@teste.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua do Teste",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )

    private fun buildCustomerUpdateDTO(
        firstName: String = "FranciscoUpdate",
        lastName: String = "Nascimento",
        income: BigDecimal = BigDecimal.valueOf(5000.0),
        zipCode: String = "3040",
        street: String = "Rua do Teste",
    ) = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )

}