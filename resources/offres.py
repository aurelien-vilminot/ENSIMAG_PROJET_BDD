import random as r

min_idcompte = 1
max_idcompte = 10

min_idproduit = 1
max_idproduit = 29

nombre_offres = 70

list_prod_prix = [-1, 39.99, 69.99, 89.99, 109.99, 69.99, 229.99, 299.00, 329.00, 529.98, 429.98, 329.98, 49.99, 19.99, 15.99, 25.99, 19.99, 9.99, 14.99, 11.99, 19.99, 10.99, 19.99, 23.99, 19.99, 14.99, 14.99, 10.99, 19.99, 19.99
]
list_prod_nb = [0 for _ in range(min_idproduit-1, max_idproduit+1)]
list_prod_tosql = [0 for _ in range(min_idproduit-1, max_idproduit+1)]

list_offers = []

DATE = "2021/11/17 10:00:00"
HOUR = 10
MIN = 0
SEC = 0

def increment_time():
    global HOUR, MIN, SEC
    SEC += 1
    if SEC == 60:
        SEC = 0
        MIN += 1
    if MIN == 60:
        MIN = 0
        HOUR += 1

def generate_fake_offer(idproduit, idcompte):
    if list_prod_nb[idproduit] == 5:
        return None
    prix = round(list_prod_prix[idproduit] + r.randint(1, 500), 2)
    list_prod_prix[idproduit] = prix
    list_prod_nb[idproduit] += 1
    return [prix, idproduit, idcompte]

def offer_to_sql(idproduit, prixoffre, idcompte):
    DATE = f"2021/11/17 10:{str(MIN).zfill(2)}:{str(SEC).zfill(2)}"
    increment_time()
    list_prod_tosql[idproduit] += 1
    sql_string = f"INSERT INTO OFFRE (idProd, dateOffre, prixOffre, idCompte) VALUES ({idproduit}, TO_DATE('{DATE}', 'yyyy/mm/dd hh24:mi:ss'), {prixoffre}, {idcompte});"
    sql_string2 = f"UPDATE PRODUIT SET PrixCProd={prixoffre} WHERE IDPROD={idproduit};"
    sql_string3 = f"INSERT INTO OFFREGAGNANTE(dateOffre, idProd) VALUES (TO_DATE('{DATE}', 'yyyy/mm/dd hh24:mi:ss'), {idproduit});"
    s = sql_string + "\n" + sql_string2
    if list_prod_tosql[idproduit] == 5:
        s += "\n" + sql_string3
    return s

def main():
    # Le 10ème user est un nouveau user qui ne fait aucune offre
    # Nombre limité d'accessoires de smartphones pour + de diversité dans les recommandations
    accessoires_smartphones = 0
    nb_acc_smart = 0
    MAX_ACC_SMART = 15
    for _ in range(nombre_offres):
        idproduit = r.randint(min_idproduit, max_idproduit)
        while (13 <= idproduit <= 29):
            idproduit = r.randint(min_idproduit, max_idproduit)
            accessoires_smartphones += 1
            if nb_acc_smart < MAX_ACC_SMART:
                break
        if (13 <= idproduit <= 29):
            nb_acc_smart += 1
        idcompte = r.randint(min_idcompte, max_idcompte - 1)
        l = generate_fake_offer(idproduit, idcompte)
        if l is not None:
            list_offers.append(l)
    list_offers.sort(key = lambda l : l[1])
    print(list_offers)
    for offer in list_offers:
        print(offer_to_sql(offer[1], offer[0], offer[2]))
    print(f"Nombre d'offres: {len(list_offers)}")
    print(f"Nombre d'offres gagnantes: {len([i for i in range(min_idproduit, max_idproduit+1) if list_prod_nb[i]==5])}")
    print(f"Liste des prix: {list_prod_prix}")
    print(f"Liste des nb: {list_prod_nb}")
    print(f"Accessoires smartphones: {nb_acc_smart}")
    return list_offers

def main2():
    l = [[374.99, 1, 8], [382.99, 1, 1], [581.99, 1, 9], [761.99, 1, 3], [533.99, 2, 7], [785.99, 2, 7], [1123.99, 2, 9], [1592.99, 2, 8], [1699.99, 2, 8], [230.99, 3, 4], [635.99, 3, 6], [789.99, 3, 4], [1150.99, 3, 1], [484.99, 4, 1], [922.99, 4, 1], [1358.99, 4, 7], [1517.99, 4, 2], [1892.99, 4, 7], [400.99, 5, 9], [423.99, 6, 2], [896.99, 6, 4], [1113.99, 6, 4], [1191.99, 6, 3], [446.0, 7, 9], [889.0, 7, 4], [1384.0, 7, 7], [1702.0, 7, 2], [1982.0, 7, 3], [388.0, 8, 7], [608.0, 8, 6], [679.0, 8, 6], [854.98, 9, 3], [1046.98, 9, 9], [1517.98, 9, 5], [651.98, 10, 5], [847.98, 10, 3], [897.98, 10, 8], [1136.98, 10, 2], [1222.98, 10, 2], [468.98, 11, 1], [510.98, 11, 5], [863.98, 11, 1], [1135.98, 11, 2], [1377.98, 11, 6], [218.99, 12, 4], [538.99, 12, 7], [97.99, 13, 7], [502.99, 13, 8], [441.99, 14, 6], [894.99, 14, 2], [66.99, 15, 4], [191.99, 17, 5], [272.99, 18, 6], [759.99, 18, 8], [984.99, 18, 1], [1365.99, 18, 1], [95.99, 19, 2], [300.99, 22, 5], [402.99, 22, 8], [392.99, 24, 9], [274.99, 28, 1]]
    for offer in l:
        print(offer_to_sql(offer[1], offer[0], offer[2]))

if __name__ == "__main__":
    main2()