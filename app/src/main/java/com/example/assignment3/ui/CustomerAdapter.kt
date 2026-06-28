package com.example.assignment3.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment3.R
import com.example.assignment3.data.model.Customer
import com.example.assignment3.data.model.UserStatus
import com.example.assignment3.databinding.ItemCustomerBinding
import java.io.File

class CustomerAdapter(
    private val onCustomerClick: (Customer) -> Unit,
    private val onKycClick: (Customer) -> Unit
) : ListAdapter<Customer, CustomerAdapter.CustomerViewHolder>(CustomerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ItemCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CustomerViewHolder(
        private val binding: ItemCustomerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(customer: Customer) {
            val context = binding.root.context

            // Bind Name
            binding.customerName.text = "${customer.firstName} ${customer.lastName}"

            // Bind Masked Account
            val last4 = if (customer.iban.length >= 4) customer.iban.takeLast(4) else customer.iban
            binding.maskedAccount.text = "A/C **** $last4"

            // Bind Balance
            binding.balanceText.text = String.format("Rs %,.0f", customer.balance)

            // Bind Image (Selfie or network avatar)
            if (customer.image.startsWith("http")) {
                Glide.with(context)
                    .load(customer.image)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .into(binding.avatarImage)
            } else {
                val file = File(customer.image)
                Glide.with(context)
                    .load(file)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .into(binding.avatarImage)
            }

            // Bind KYC Status
            if (customer.status == UserStatus.COMPLETED) {
                binding.statusBadge.text = "VERIFIED"
                binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.accent_verified))
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_verified)
                binding.kycButton.visibility = View.GONE
            } else {
                binding.statusBadge.text = "PENDING"
                binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.accent_pending))
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_pending)
                binding.kycButton.visibility = View.VISIBLE
            }

            // Click Listeners
            binding.root.setOnClickListener { onCustomerClick(customer) }
            binding.kycButton.setOnClickListener { onKycClick(customer) }
        }
    }

    class CustomerDiffCallback : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }
    }
}
