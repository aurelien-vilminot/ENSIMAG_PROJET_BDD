import random as r

min_idcompte = 1
max_idcompte = 10

min_idproduit = 1
max_idproduit = 29

nombre_offres = 100

list_prod_prix = [-1, 39.99, 69.99, 89.99, 109.99, 69.99, 229.99, 299.00, 329.00, 529.98, 429.98, 329.98, 49.99, 19.99, 15.99, 25.99, 19.99, 9.99, 14.99, 11.99, 19.99, 10.99, 19.99, 23.99, 19.99, 14.99, 14.99, 10.99, 19.99, 19.99
]
list_prod_nb = [0 for i in range(min_idproduit-1, max_idproduit+1)]


list_offers = []

def generate_fake_offer(idproduit, idcompte):
    if list_prod_nb[idproduit] == 5:
        return None
    prix = round(list_prod_prix[idproduit] + r.randint(1, 500), 2)
    list_prod_prix[idproduit] = prix
    list_prod_nb[idproduit] += 1
    return [prix, idproduit, idcompte]

def main():
    for _ in range(nombre_offres):
        idproduit = r.randint(min_idproduit, max_idproduit)
        idcompte = r.randint(min_idcompte, max_idcompte)
        l = generate_fake_offer(idproduit, idcompte)
        if l is not None:
            list_offers.append(l)
    list_offers.sort(key = lambda l : l[1])
    print(list_offers)
    print(f"Nombre d'offres: {len(list_offers)}")
    print(f"Nombre d'offres gagnantes: {len([i for i in range(min_idproduit, max_idproduit+1) if list_prod_nb[i]==5])}")
    print(f"Liste des prix: {list_prod_prix}")
    print(f"Liste des nb: {list_prod_nb}")
    return list_offers

if __name__ == "__main__":
    main()