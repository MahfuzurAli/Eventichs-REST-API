package eventichs.api.eventichs_api.Controleurs

import eventichs.api.eventichs_api.Exceptions.RessourceInexistanteException
import eventichs.api.eventichs_api.Modèle.InvitationOrganisation
import eventichs.api.eventichs_api.Services.InvitationOrganisationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc


import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import com.fasterxml.jackson.databind.ObjectMapper
import eventichs.api.eventichs_api.Exceptions.ConflitAvecUneRessourceExistanteException
import eventichs.api.eventichs_api.Modèle.Organisation
import eventichs.api.eventichs_api.Modèle.Utilisateur
import org.hamcrest.CoreMatchers.containsString
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders


@SpringBootTest
@AutoConfigureMockMvc
class InvitationOrganisationControleurTest {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    lateinit var service: InvitationOrganisationService

    @Autowired
    private lateinit var mockMvc: MockMvc



    // -----------------------------------------------------------------------------------------------------------------
    //
    // @GetMapping("/organisations/invitations/{id}")
    //
    // -----------------------------------------------------------------------------------------------------------------
    @WithMockUser("Anonym")
    @Test // obtenirInvitationsParId() 200 OK
    fun `1- Étant donné une invitation ayant l'id 1 qui existe dans le service lorsqu'on effectue une requête GET de recherche par id alors on obtient un JSON qui contient l'invitation dont l'id est 1 et un code de retour 200` (){
        val invitation = InvitationOrganisation(
            1,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                1,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "envoyé")

        Mockito.`when`(service.chercherParID(1,"Anonyme")).thenReturn(invitation)

        mockMvc.perform(get("/organisations/invitations/1").with(csrf()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.utilisateur.code").value("auth0|656d3ecc4178aefc03429538"))
            .andExpect(jsonPath("$.utilisateur.nom").value("nomUtil"))
            .andExpect(jsonPath("$.utilisateur.prénom").value("prénom"))
            .andExpect(jsonPath("$.utilisateur.email").value("email"))
            .andExpect(jsonPath("$.organisation.id").value("1"))
            .andExpect(jsonPath("$.organisation.idUtilisateur").value("1"))
            .andExpect(jsonPath("$.organisation.nomOrganisation").value("nomOrg"))
            .andExpect(jsonPath("$.organisation.catégorie_id").value("1"))
            .andExpect(jsonPath("$.organisation.estPublic").value(true))
            .andExpect(jsonPath("$.jeton").value(null))
            .andExpect(jsonPath("$.status").value("envoyé"))
    }

    @WithMockUser("Anonym")
    @Test  // obtenirInvitationsParId() 404 isNotFound
    fun `2- Étant donné l'invitation dont l'id est 8 et qui n'est pas inscrite au service lorsqu'on effectue une requête GET de recherche par id  alors on obtient un code de retour 404 et le message d'erreur «L'invitation 8 à une organisation n'est pas inscrit au service»`() {
        Mockito.`when`(service.chercherParID(8,"Anonym")).thenReturn(null)

        mockMvc.perform(get("/organisations/invitations/8")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is RessourceInexistanteException)
                assertEquals("L'invitation 8 à une organisation n'est pas inscrit au service", résultat.resolvedException?.message)
            }
    }


    // -----------------------------------------------------------------------------------------------------------------
    //
    // @PostMapping("/organisations/invitations/")
    // Cas d'utilisation: 1.Demander à joindre une organisation (Participant)
    //
    // -----------------------------------------------------------------------------------------------------------------
    @WithMockUser("Anonym")
    @Test  //demandeJoindreOrganisation() 201 isCreated
    fun `3- Étant donnée une invitation à une organisation dont l'id de l'organisation est 1 et celui du participant est auth0|656d3ecc4178aefc03429538 lorsqu'on effectue une requête POST pour l'ajouter alors on obtient un code de retour 201 et un JSON qui contient l'invitation créé`(){
        val invitation = InvitationOrganisation(
            1,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                1,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "envoyé")

        Mockito.`when`(service.demandeJoindreOrganisation(invitation,"auth0|656d3ecc4178aefc03429538")).thenReturn(invitation)

        mockMvc.perform(MockMvcRequestBuilders.post("/organisations/invitations/").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(invitation)))
            .andExpect(status().isCreated)
            .andExpect(header().string("Location",containsString("/organisations/invitations/1"))) /*
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.utilisateur.code").value("auth0|656d3ecc4178aefc03429538"))
            .andExpect(jsonPath("$.utilisateur.nom").value("nomUtil"))
            .andExpect(jsonPath("$.utilisateur.prénom").value("prénom"))
            .andExpect(jsonPath("$.utilisateur.email").value("email"))
            .andExpect(jsonPath("$.organisation.id").value("1"))
            .andExpect(jsonPath("$.organisation.idUtilisateur").value("1"))
            .andExpect(jsonPath("$.organisation.nomOrganisation").value("nomOrg"))
            .andExpect(jsonPath("$.organisation.catégorie_id").value("1"))
            .andExpect(jsonPath("$.organisation.estPublic").value(true))
            .andExpect(jsonPath("$.jeton").value(null))
            .andExpect(jsonPath("$.status").value("envoyé"))*/
    }

    @WithMockUser("Anonym")
    @Test  //demandeJoindreOrganisation() 404 notFound (organisation inxesitante)
    fun `4- Étant donnée une invitation à une organisation dont l'id de l'organisation est 18 mais celle-ci n'est pas inscrite au service lorsqu'on effectue une requête POST pour l'ajouter alors on obtient un code de retour 404 et le message d'erreur «L'organisation 18 n'existe pas»`(){
        val invitation = InvitationOrganisation(
            1,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                18,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "envoyé")
        Mockito.`when`(service.demandeJoindreOrganisation(invitation,"auth0|656d3ecc4178aefc03429538")).thenThrow(RessourceInexistanteException("L'organisation 18 n'existe pas"))

        mockMvc.perform(post("/organisations/invitations/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(invitation)))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is RessourceInexistanteException)
                assertEquals("L'organisation 18 n'existe pas", résultat.resolvedException?.message)
            }
    }

    @WithMockUser("Anonym")
    @Test  //demandeJoindreOrganisation() 404 notFound (participant inxesitant)
    fun `5- Étant donnée une invitation à une organisation dont l'id du participant est 23 mais celui-ci n'est pas inscrit au service lorsqu'on effectue une requête POST pour l'ajouter alors on obtient un code de retour 404 et le message d'erreur «Le participant 23 n'existe pas»`(){
        val invitation = InvitationOrganisation(
            1,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                3,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "envoyé")
        Mockito.`when`(service.demandeJoindreOrganisation(invitation,"auth0|656d3ecc4178aefc03429538")).thenThrow(RessourceInexistanteException("Le participant 23 n'existe pas"))

        mockMvc.perform(post("/organisations/invitations/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(invitation)))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is RessourceInexistanteException)
                assertEquals("Le participant 23 n'existe pas", résultat.resolvedException?.message)
            }
    }


    @WithMockUser("Anonym")
    @Test  //demandeJoindreOrganisation() 409 isConflict
    fun `6- Étant donnée une invitation à une organisation dont l'id de l'organisation est 3 et celui du participant est 1 et qui est déjà inscrite au service lorsqu'on effectue une requête POST pour l'ajouter alors on obtient un code de retour 409 et et le message «Il y existe déjà une invitation à l'organisation nomOrg assigné au participant prénom nomUtil inscrit au service»` () {
        val invitation = InvitationOrganisation(
            1,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                3,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "envoyé")
        Mockito.`when`(service.demandeJoindreOrganisation(invitation,"auth0|656d3ecc4178aefc03429538"))
            .thenThrow(ConflitAvecUneRessourceExistanteException("Il y existe déjà une invitation à l'organisation nomOrg assigné au participant prénom nomUtil inscrit au service"))

        mockMvc.perform(post("/organisations/invitations/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(invitation)))
            .andExpect(status().isConflict)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is ConflitAvecUneRessourceExistanteException)
                assertEquals(
                    "Il y existe déjà une invitation à l'organisation nomOrg assigné au participant prénom nomUtil inscrit au service",
                    résultat.resolvedException?.message
                )
            }
    }


    // -----------------------------------------------------------------------------------------------------------------
    //
    // @DeleteMapping("/organisations/invitations/{idInvitationOrganisation}")
    // Cas d'utilisation: 7.Éffacer une invitation (Participant + Organisation)
    //
    // -----------------------------------------------------------------------------------------------------------------
    @WithMockUser("Anonym")
    @Test //effacerInvitation() 200 ok
    fun `7- Étant donné une invitation avec l'id 3 à une organisation inscrite au service lorsqu'on effectue une requête DELETE selon l'id 3 alors on obtient un JSON qui contient l'invitation effacé et un code de retour 200` (){
        val invitation = InvitationOrganisation(
            3,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                3,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "envoyé")
        Mockito.`when`(service.effacerInvitation(3,"Anonym")).thenReturn(invitation)

        mockMvc.perform(delete("/organisations/invitations/3"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.utilisateur.code").value("auth0|656d3ecc4178aefc03429538"))
            .andExpect(jsonPath("$.utilisateur.nom").value("nomUtil"))
            .andExpect(jsonPath("$.utilisateur.prénom").value("prénom"))
            .andExpect(jsonPath("$.utilisateur.email").value("email"))
            .andExpect(jsonPath("$.organisation.id").value("3"))
            .andExpect(jsonPath("$.organisation.idUtilisateur").value("1"))
            .andExpect(jsonPath("$.organisation.nomOrganisation").value("nomOrg"))
            .andExpect(jsonPath("$.organisation.catégorie_id").value("1"))
            .andExpect(jsonPath("$.organisation.estPublic").value(true))
            .andExpect(jsonPath("$.jeton").value(null))
            .andExpect(jsonPath("$.status").value("envoyé"))
    }

    @WithMockUser("Anonym")
    @Test //effacerInvitation() 404 notFound
    fun `8- Étant donné une invitation avec l'id 3 à une organisation pas inscrite au service lorsqu'on effectue une requête DELETE selon l'id 3 alors on obtient un code de retour 404 et le message d'erreur «L'invitation 3 à une organisation n'est pas inscrit au service»` (){
        Mockito.`when`(service.effacerInvitation(3,"Anonym")).thenThrow(RessourceInexistanteException("L'invitation 3 à une organisation n'est pas inscrit au service"))

        mockMvc.perform(delete("/organisations/invitations/3"))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is RessourceInexistanteException)
                assertEquals("L'invitation 3 à une organisation n'est pas inscrit au service", résultat.resolvedException?.message)
            }
    }

    // -----------------------------------------------------------------------------------------------------------------
    //
    // @GetMapping("/utilisateurs/{idParticipant}/invitations")
    // Cas d'utilisation: 3.Consulter ses invitations(Participant)
    //
    // -----------------------------------------------------------------------------------------------------------------
    @WithMockUser("Anonym")
    @Test // obtenirInvitationParticipant() 200 ok
    fun `9- Étant donné un participant ayant l'id 1 inscrit au service qui a une invitation lorsqu'on effectue une requête GET de recherche par participant selon l'id 1 alors on obtient un JSON d'une liste qui contient une InvitationOrganisation ayant l'id 5 et un code de retour 200` (){
        val listeInvitations : List<InvitationOrganisation> = listOf(InvitationOrganisation(
            5,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                3,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "envoyé"))

        Mockito.`when`(service.chercherParParticipant("Anonym")).thenReturn(listeInvitations)

        mockMvc.perform(get("/utilisateurs/1/invitations/organisations"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(5))
            .andExpect(jsonPath("$.utilisateur.code").value("auth0|656d3ecc4178aefc03429538"))
            .andExpect(jsonPath("$.utilisateur.nom").value("nomUtil"))
            .andExpect(jsonPath("$.utilisateur.prénom").value("prénom"))
            .andExpect(jsonPath("$.utilisateur.email").value("email"))
            .andExpect(jsonPath("$[0].organisation.id").value("3"))
            .andExpect(jsonPath("$[0].organisation.idUtilisateur").value("1"))
            .andExpect(jsonPath("$[0].organisation.nomOrganisation").value("nomOrg"))
            .andExpect(jsonPath("$[0].organisation.catégorie_id").value("1"))
            .andExpect(jsonPath("$[0].organisation.estPublic").value(true))
            .andExpect(jsonPath("$[0].jeton").value(null))
            .andExpect(jsonPath("$[0].status").value("envoyé"))
    }

    @WithMockUser("Anonym")
    @Test // obtenirInvitationParticipant() 404 notFound
    fun `10- Étant donné un participant ayant l'id 1 qui n'est pas inscrite au service qui a une invitation lorsqu'on effectue une requête GET de recherche par participant selon l'id 1 alors on obtient un code de retour 404 et le message d'erreur «Le participant 1 n'existe pas»` (){
        Mockito.`when`(service.chercherParParticipant("Anonym")).thenThrow(RessourceInexistanteException("Le participant 1 n'existe pas"))

        mockMvc.perform(get("/utilisateurs/1/invitations/organisations"))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is RessourceInexistanteException)
                assertEquals("Le participant 1 n'existe pas", résultat.resolvedException?.message)
            }
    }

    // -----------------------------------------------------------------------------------------------------------------
    //
    // @GetMapping("/organisations/{idOrganisation}/invitations")
    // Cas d'utilisation: 3.Consulter ses invitations(Organisation)
    //
    // -----------------------------------------------------------------------------------------------------------------
    @WithMockUser("Anonym")
    @Test // obtenirInvitationOrganisation() 200 ok
    fun `11- Étant donné une organisation ayant l'id 1 inscrit au service qui a une invitation lorsqu'on effectue une requête GET de recherche par organisation selon l'id 1 alors on obtient un JSON d'une liste qui contient une InvitationOrganisation ayant l'id 5 et un code de retour 200` (){
        val listeInvitations : List<InvitationOrganisation> = listOf(InvitationOrganisation(
            5,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                3,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "envoyé"))

        Mockito.`when`(service.chercherParOrganisation(1,"Anonym")).thenReturn(listeInvitations)

        mockMvc.perform(get("/organisations/1/invitations"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(5))
            .andExpect(jsonPath("$.utilisateur.code").value("auth0|656d3ecc4178aefc03429538"))
            .andExpect(jsonPath("$.utilisateur.nom").value("nomUtil"))
            .andExpect(jsonPath("$.utilisateur.prénom").value("prénom"))
            .andExpect(jsonPath("$.utilisateur.email").value("email"))
            .andExpect(jsonPath("$[0].organisation.id").value("3"))
            .andExpect(jsonPath("$[0].organisation.idUtilisateur").value("1"))
            .andExpect(jsonPath("$[0].organisation.nomOrganisation").value("nomOrg"))
            .andExpect(jsonPath("$[0].organisation.catégorie_id").value("1"))
            .andExpect(jsonPath("$[0].organisation.estPublic").value(true))
            .andExpect(jsonPath("$[0].jeton").value(null))
            .andExpect(jsonPath("$[0].status").value("envoyé"))
    }

    @WithMockUser("Anonym")
    @Test // obtenirInvitationOrganisation() 404 notFound
    fun `12- Étant donné une organisation ayant l'id 1 qui n'est pas inscrite au service qui a une invitation lorsqu'on effectue une requête GET de recherche par organisation selon l'id 1 alors on obtient un code de retour 404 et le message d'erreur «L'organisation 1 n'existe pas»` (){
        Mockito.`when`(service.chercherParOrganisation(1,"Anonym")).thenThrow(RessourceInexistanteException("L'organisation 1 n'existe pas"))

        mockMvc.perform(get("/organisations/1/invitations"))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is RessourceInexistanteException)
                assertEquals("L'organisation 1 n'existe pas", résultat.resolvedException?.message)
            }
    }

    // -----------------------------------------------------------------------------------------------------------------
    //
    // @PutMapping("/organisations/invitations/{id}/status/{status}")
    // Cas d'utilisation: 4.Accepter la demande de joindre l'organisation par le participant (Organisation)
    //
    // -----------------------------------------------------------------------------------------------------------------
    @WithMockUser("Anonym")
    @Test // changerStatus() 200 ok -> vert
    fun `13- Étant donnée une invitation à une organisation ayant l'id 8 inscrite au service lorsqu'on effectue une requête PUT pour changer son status à "accepté" alors on obtient un code de retour 200 et un JSON qui contient l'invitation modifié`(){
        val invitation = InvitationOrganisation(
            8,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                1,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "accepté")

        Mockito.`when`(service.changerStatus(invitation, "accepté","Anonym")).thenReturn(invitation)

        mockMvc.perform(put("/organisations/invitations/8/status/accepté")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(invitation)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(8))
            .andExpect(jsonPath("$.utilisateur.code").value("auth0|656d3ecc4178aefc03429538"))
            .andExpect(jsonPath("$.utilisateur.nom").value("nomUtil"))
            .andExpect(jsonPath("$.utilisateur.prénom").value("prénom"))
            .andExpect(jsonPath("$.utilisateur.email").value("email"))
            .andExpect(jsonPath("$.organisation.id").value("1"))
            .andExpect(jsonPath("$.organisation.idUtilisateur").value("1"))
            .andExpect(jsonPath("$.organisation.nomOrganisation").value("nomOrg"))
            .andExpect(jsonPath("$.organisation.catégorie_id").value("1"))
            .andExpect(jsonPath("$.organisation.estPublic").value(true))
            .andExpect(jsonPath("$.jeton").value(null))
            .andExpect(jsonPath("$.status").value("accepté"))
    }

    @WithMockUser("Anonym")
    @Test // changerStatus() 404 notFound
    fun `14- Étant donnée une invitation à une organisation ayant l'id 8 qui n'est pas inscrite au service lorsqu'on effectue une requête PUT pour changer son status à "accepté" alors on obtient un code de retour 404 et le message d'erreur «L'invitation 8 à une organisation n'est pas inscrit au service»`(){
        val invitation = InvitationOrganisation(
            3,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                3,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            null,
            "envoyé")

        Mockito.`when`(service.changerStatus(invitation, "accepté","Anonym")).thenThrow(RessourceInexistanteException("L'invitation 8 à une organisation n'est pas inscrit au service"))

        mockMvc.perform(put("/organisations/invitations/8/status/accepté"))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is RessourceInexistanteException)
                assertEquals("L'invitation 8 à une organisation n'est pas inscrit au service", résultat.resolvedException?.message)
            }
    }

    // -----------------------------------------------------------------------------------------------------------------
    //
    // @PutMapping("/organisations/jetons/{jeton}")
    // Cas d'utilisation: 5.Entrer un jeton d'invitation (Participant)
    //
    // -----------------------------------------------------------------------------------------------------------------
    @WithMockUser("Anonym")
    @Test // saisirJeton() 200 ok
    fun `15- Étant donnée une invitation à une organisation ayant le jeton 'VF5S6H55' inscrite au service lorsqu'on effectue une requête PUT pour changer le status à "accepté" de l'invitation ayant le jeton 'VF5S6H55' alors on obtient un code de retour 200 et un JSON qui contient l'invitation modifié`(){
        val invitation = InvitationOrganisation(
            8,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                1,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            "VF5S6H55",
            "accepté")

        Mockito.`when`(service.saisirJeton("VF5S6H55","Anonym")).thenReturn(invitation)

        mockMvc.perform(put("/organisations/jetons/VF5S6H55")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(invitation.Utilisateur!!)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(8))
            .andExpect(jsonPath("$.utilisateur.code").value("auth0|656d3ecc4178aefc03429538"))
            .andExpect(jsonPath("$.utilisateur.nom").value("nomUtil"))
            .andExpect(jsonPath("$.utilisateur.prénom").value("prénom"))
            .andExpect(jsonPath("$.utilisateur.email").value("email"))
            .andExpect(jsonPath("$.organisation.id").value("1"))
            .andExpect(jsonPath("$.organisation.idUtilisateur").value("1"))
            .andExpect(jsonPath("$.organisation.nomOrganisation").value("nomOrg"))
            .andExpect(jsonPath("$.organisation.catégorie_id").value("1"))
            .andExpect(jsonPath("$.organisation.estPublic").value(true))
            .andExpect(jsonPath("$.jeton").value("VF5S6H55"))
            .andExpect(jsonPath("$.status").value("accepté"))
    }

    @WithMockUser("Anonym")
    @Test // saisirJeton() 404 notFound
    fun `16- Étant donnée une invitation à une organisation ayant un jeton 'VF5S6H55' qui n'est pas inscrite au service lorsqu'on effectue une requête PUT pour changer son status à "accepté" de l'invitation ayant le jeton 'VF5S6H55' alors on obtient un code de retour 404 et le message d'erreur «Aucune invitation inscrit dans le service contient le jeton VF5S6H55»`(){
        val invitation = InvitationOrganisation(
            8,
            Utilisateur(
                "auth0|656d3ecc4178aefc03429538",
                "email",
                "nomUtil",
                "prénom"),
            Organisation(
                1,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            "VF5S6H55",
            "accepté")

        Mockito.`when`(service.saisirJeton("VF5S6H55","Anonym")).thenThrow(RessourceInexistanteException("Aucune invitation inscrit dans le service contient le jeton VF5S6H55"))

        mockMvc.perform(put("/organisations/jetons/VF5S6H55")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(invitation.Utilisateur!!)))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is RessourceInexistanteException)
                assertEquals("Aucune invitation inscrit dans le service contient le jeton VF5S6H55", résultat.resolvedException?.message)
            }
    }

    // -----------------------------------------------------------------------------------------------------------------
    //
    // @PostMapping("/organisations/{idOrganisation}/jetons")
    // Cas d'utilisation: 6.Générer son jeton d'invitation
    //
    // -----------------------------------------------------------------------------------------------------------------
    @WithMockUser("Anonym")
    @Test // crééJeton() 200 ok
    fun `17- Étant donné une organisation ayant l'id 3 inscrit au service qui a une invitation lorsqu'on effectue une requête POST pour créer une invitation assigné à aucun utilisateur et avec un jeton généré pour l'organisation avec l'id 3 alors on obtient un JSON d'une liste qui contient une InvitationOrganisation ayant un jeton généré et assigné à aucun utilisateur  et un code de retour 201` (){
        val invitation = InvitationOrganisation(
            54,
            null,
            Organisation(
                3,
                "auth0|656d2dbea19599c9209a4f01",
                "nomOrg",
                1,
                true),
            "OYX4J399",
            "généré")

        Mockito.`when`(service.crééJeton(invitation.Organisation,"Anonym")).thenReturn(invitation)

        mockMvc.perform(post("/organisations/3/jetons"))
            .andExpect(status().isCreated)
            .andExpect(header().string("Location",containsString("/organisations/invitations/54")))
            .andExpect(jsonPath("$.id").value(54))
            .andExpect(jsonPath("$.utilisateur").value(null))
            .andExpect(jsonPath("$.organisation.id").value("3"))
            .andExpect(jsonPath("$.organisation.idUtilisateur").value("1"))
            .andExpect(jsonPath("$.organisation.nomOrganisation").value("nomOrg"))
            .andExpect(jsonPath("$.organisation.catégorie_id").value("1"))
            .andExpect(jsonPath("$.organisation.estPublic").value(true))
            .andExpect(jsonPath("$.jeton").value("OYX4J399"))
            .andExpect(jsonPath("$.status").value("généré"))
    }
    @WithMockUser("Anonym")
    @Test // crééJeton() 404 notFound
    fun `18- Étant donné une organisation ayant l'id 36 qui n'est pas inscrite au service lorsqu'on effectue une requête POST pour créer une invitation assigné à aucun utilisateur et avec un jeton généré pour l'organisation avec l'id 36 alors on obtient un code de retour 404 et le message d'erreur «L'organisation 36 n'existe pas»` (){
        val organisation = Organisation(
            1,
            "auth0|656d2dbea19599c9209a4f01",
            "nomOrg",
            1,
            true)

        Mockito.`when`(service.crééJeton(organisation,"Anonym")).thenThrow(RessourceInexistanteException("L'organisation 36 n'existe pas"))

        mockMvc.perform(post("/organisations/36/jetons"))
            .andExpect(status().isNotFound)
            .andExpect { résultat ->
                assertTrue(résultat.resolvedException is RessourceInexistanteException)
                assertEquals("L'organisation 36 n'existe pas", résultat.resolvedException?.message)
            }
    }

}