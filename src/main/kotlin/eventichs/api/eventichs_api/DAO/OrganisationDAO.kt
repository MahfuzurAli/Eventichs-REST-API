package eventichs.api.eventichs_api.DAO

import eventichs.api.eventichs_api.Modèle.Organisation

interface OrganisationDAO: DAO<Organisation> {
    override fun chercherTous(): List<Organisation>
    override fun chercherParID(id: Int): Organisation?
    fun ajouter(element: Organisation, codeUtilisateur: String): Organisation?
    override fun supprimerParID(id: Int): Organisation?
    override fun modifier(element: Organisation): Organisation?
    fun consulterOrganisationPubliques(): List<Organisation>
    fun filtrerOrganisationParGouts(idCategorie: Int): List<Organisation>
    fun validerUtilisateur(id: Int, codeUtilisateur: String) : Boolean
    fun validerOrganisationCreation(codeUtilisateur: String): Boolean

    // TO DO CHECK IF ORGANISATION IS PUBLIC OR NOT


}