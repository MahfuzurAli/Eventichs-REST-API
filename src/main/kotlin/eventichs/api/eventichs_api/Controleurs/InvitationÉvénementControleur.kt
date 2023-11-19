package eventichs.api.eventichs_api.Controleurs

import eventichs.api.eventichs_api.Modèle.InvitationOrganisation
import eventichs.api.eventichs_api.Modèle.InvitationÉvénement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.base-path:}")
class InvitationÉvénementControleur() {

    //Cas d'utilisation: 2.Inviter un autre participant à un événement publique (Participant)
    //Cas d'utilisation: 3.Consulter ses invitations(Participant+Organisation)
    //Cas d'utilisation: 5.Entrer un jeton d'invitation (Participant)
    //Cas d'utilisation: 6.Générer son jeton d'invitation (Organisation)
    //Cas d'utilisation: 7.Éffacer une invitation (Participant + Organisation)



    //si l'utilisateur est un participant, cela affiche des invitations. si il est une organisation, cela affiche des demandes d'invitations.
    //@GetMapping("/invitations/{id}")
    fun obtenirInvitationsParIdUtilisateur(@PathVariable id: String):
            ResponseEntity<InvitationOrganisation> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)

    //@PostMapping("/invitations")
    fun inviterOuDemanderInvitation(@RequestBody invitationOuDemande: InvitationOrganisation):
            ResponseEntity<InvitationOrganisation> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)

    //@PutMapping("/invitation/{id}")
    fun majInvitation(@PathVariable id: String, @RequestBody reponse: String):
            ResponseEntity<InvitationOrganisation> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)

    //@DeleteMapping("/invitation/{id}")
    fun supprimerInvitation(@PathVariable id: String):
            ResponseEntity<InvitationOrganisation> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)


    /*
    @PostMapping("/jetons/{utilisateur_id}")
    fun saisirJeton(@PathVariable utilisateur_id : Int, @RequestBody jeton : InvitationÉvénement):
            ResponseEntity<InvitationOrganisation> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)

    @GetMapping("/jetons/{id}/{quantité}")
    fun générerJeton(@PathVariable id : Int,  @PathVariable quantité : Int):
            ResponseEntity<InvitationOrganisation> = ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
     */
}
