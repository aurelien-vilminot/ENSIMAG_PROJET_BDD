import random as r

min_idcompte = 1
max_idcompte = 10

min_idproduit = 1
max_idproduit = 29

nombre_offres = 100

dict_prod_prix = {}
dict_prod_nb = {}

list_offers = []

def generate_fake_offer(idproduit, idcompte):
    try:
        prix = dict_prod_prix[idproduit] + r.randint(100, 500)
    except KeyError:
        prix = r.randint(1000, 5000)
        dict_prod_nb[idproduit] = 0
    dict_prod_nb[idproduit] += 1
    dict_prod_prix[idproduit] = prix
    return [prix, idproduit, idcompte]



def main():
    for _ in range(nombre_offres):
        idproduit = r.randint(min_idproduit, max_idproduit)
        idcompte = r.randint(min_idcompte, max_idcompte)
        try:
            if dict_prod_nb[idproduit] == 5:
                continue
        except KeyError:
            pass
        list_offers.append(generate_fake_offer(idproduit, idcompte))
    return list_offers

if __name__ == "__main__":
    print(main())