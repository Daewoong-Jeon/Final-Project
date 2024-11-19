<template>

    <v-card outlined>
        <v-card-title>
            BookList # {{$route.params.id }}
        </v-card-title>

        <v-card-text>
            <div>
                <String label="BookId" v-model="item.bookId" :editMode="editMode" @change="change" />
            </div>
            <div>
                <String label="RentalStatus" v-model="item.rentalStatus" :editMode="editMode" @change="change" />
            </div>
            <div>
                <String label="RecentRentalMemberId" v-model="item.recentRentalMemberId" :editMode="editMode" @change="change" />
            </div>
            <div>
                <Number label="RentalCost" v-model="item.rentalCost" :editMode="editMode" @change="change" />
            </div>
            <div>
                <Date label="RecentRentalDate" v-model="item.recentRentalDate" :editMode="editMode" @change="change" />
            </div>
            <div>
                <Date label="RequiredReturnDate" v-model="item.requiredReturnDate" :editMode="editMode" @change="change" />
            </div>
        </v-card-text>
    </v-card>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'BookListViewDetail',
    props: {
      value: Object,
    },
    data: () => ({
        item : [],
    }),
    async created() {
      var params = this.$route.params;
      var temp = await axios.get(axios.fixUrl('/bookLists/' + params.id))

      this.item = temp.data;

    },
    methods: {
    }
  }
</script>

