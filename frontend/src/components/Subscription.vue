<template>
    <div>
        <Boolean
            label="구독 여부"
            v-model="value.isSubscription"
            :editMode="editMode"
        />
        <Date
            label="대여시작기간"
            v-model="value.rentalstart"
            :editMode="editMode"
        />
        <Date
            label="대여종료기간"
            v-model="value.rentalend"
            :editMode="editMode"
        />
        <String
            label="도서 URL"
            v-model="value.webUrl"
            :editMode="editMode"
        />
        <BookId
            offline
            label="도서 ID"
            v-model="value.bookId"
            :editMode="editMode"
            @change="change"
        />
        <UserId
            offline
            label="구독자 ID"
            v-model="value.userId"
            :editMode="editMode"
            @change="change"
        />
        <v-row class="ma-0 pa-0">
            <v-spacer></v-spacer>
            <v-btn width="64px" color="primary" @click="save">
                저장
            </v-btn>
        </v-row>
    </div>
</template>


<script>
import BaseEntity from './base-ui/BaseEntity.vue'

export default {
    name: 'Subscription',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "subscriptions",
        value: {
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
    },
}
</script>
