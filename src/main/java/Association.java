import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER;

@Data
@NoArgsConstructor
@JsonPropertyOrder({
        "codeGestionnaire",
        "ancienNumero",
        "siret",
        "codePrefectureOuSousPrefectureDuBureauGestionnaire",
        "dateDeCreation",
        "dateDePublication",
        "nature",
        "groupement",
        "titre",
        "objet",
        "objetSocialWaldec1",
        "objetSocialWaldec2",
        "adresseLigne1",
        "adresseLigne2",
        "adresseLigne3",
        "codePostal",
        "commune",
        "codeInsee",
        "siteWeb",
        "observation",
        "position",
        "numeroRUPMinistere",
        "dateDeMiseAJour"
})
class Association {
    /**
     * code gestionnaire  + Id_ex = Numéro de l’association
     */
    private String codeGestionnaire;
    /**
     * n° dans l’ancien  SI = Ancien numéro de l’association
     */
    private String ancienNumero;
    private String siret;
    /**
     * code de la Préfecture ou Sous-préfecture du Bureau gestionnaire = Code du site gestionnaire de l’association
     */
    private String codePrefectureOuSousPrefectureDuBureauGestionnaire;
    /**
     * ‘0001-01-01’ = Date de déclaration de création (date de dépôt du dossier en Préfecture)
     */
    private Date dateDeCreation;
    /**
     * ‘0001-01-01’ = Date de publication JO de l’avis de création
     */
    private Date dateDePublication;
    /**
     * code selon simplement déclaré =  RUP, assistance, bienfaisance, cultuelle
     */
    private Character nature;
    /**
     * code selon = assoc. simple, union ou fédération
     */
    private Character groupement;
    /**
     * Titre de l’association
     */
    private String titre;
    /**
     * Objet de l’association
     */
    private String objet;
    /**
     * code objet social Waldec 1
     */
    private String objetSocialWaldec1;
    /**
     * code objet social Waldec 2
     */
    private String objetSocialWaldec2;
    /**
     * adresse de l’association
     */
    private String adresseLigne1;
    /**
     * adresse de l’association
     */
    private String adresseLigne2;
    /**
     * adresse de l’association
     */
    private String adresseLigne3;
    /**
     * « 00000 » = code postal
     */
    private String codePostal;
    /**
     * libéllée commune
     */
    private String commune;
    /**
     * « 00000» = code insee commune
     */
    private String codeInsee;
    /**
     * site web de l’association
     */
    private String siteWeb;
    /**
     * champs libre
     */
    private String observation;
    /**
     * « A »
     */
    private String position;
    /**
     * N° de RUP attribué par le Ministère
     */
    private String numeroRUPMinistere;
    /**
     * ‘timestamp***’ = Date de mise à jour de l’article
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = NUMBER)
    private Date dateDeMiseAJour;
}
