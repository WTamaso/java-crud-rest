package dev.wtamaso.addressbook.api;

import com.google.gson.Gson;
import dev.wtamaso.addressbook.dto.AddressDTO;
import dev.wtamaso.addressbook.dto.ResponseDTO;
import dev.wtamaso.addressbook.entities.Address;
import dev.wtamaso.addressbook.service.AddressService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Addresses API", urlPatterns = {"/api/addresses/", "/api/addresses/*"})
public class AddressApi extends HttpServlet {

    private void successResponse(Long entityId, String message, Object response, Integer amount, int code, HttpServletResponse res) throws IOException {
        res.setStatus(code);
        res.setContentType("application/JSON");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        ResponseDTO dto;
        if (entityId != null && message != null) {
            dto = new ResponseDTO(code, entityId, message);
        } else if (response != null) {
            dto = new ResponseDTO(code, response, amount);
        } else {
            dto = new ResponseDTO(HttpServletResponse.SC_NO_CONTENT, "Empty response", "There is no result for your request.");
        }

        Gson gson = new Gson();

        out.print(gson.toJson(dto));
        out.close();
    }

    private void errorResponse(String message, String reason, int code, HttpServletResponse res) throws IOException {
        res.setStatus(code);
        res.setContentType("application/JSON");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        ResponseDTO dto = new ResponseDTO(code, message, reason);
        Gson gson = new Gson();

        out.print(gson.toJson(dto));
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");
            if (req.getPathInfo() != null) {
                try {
                    Long id = Long.parseLong(req.getPathInfo().replaceFirst("/", ""));

                    Address result = AddressService.get(id);
                    if (result == null) {
                        errorResponse("Can't find address", "Address not found with the given parameter.", HttpServletResponse.SC_NOT_FOUND, res);
                    }

                    successResponse(null, null, AddressDTO.getDto(result), null, HttpServletResponse.SC_OK, res);
                } catch (Exception ex) {
                    errorResponse("Can't find address", "Invalid parameter.", HttpServletResponse.SC_BAD_REQUEST, res);
                    return;
                }
            } else {
                List<Address> resultList = AddressService.getList();
                List<AddressDTO> dtoList = new ArrayList<>();

                resultList.forEach(address -> dtoList.add(AddressDTO.getDto(address)));

                successResponse(null, null, dtoList, dtoList.size(), HttpServletResponse.SC_OK, res);
            }
        } catch (Exception e) {
            errorResponse("Failed to process request", e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, res);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");
            if (req.getPathInfo() != null) {
                errorResponse("Not allowed", "Invalid parameter.", HttpServletResponse.SC_METHOD_NOT_ALLOWED, res);
                return;
            }

            Gson gson = new Gson();
            AddressDTO dto = gson.fromJson(req.getReader(), AddressDTO.class);

            if (dto == null) {
                errorResponse("Can't create address", "No data submitted.", HttpServletResponse.SC_BAD_REQUEST, res);
                return;
            }
            if (!AddressService.validate(dto)) {
                errorResponse("Validation error", "Required fields not informed.", HttpServletResponse.SC_BAD_REQUEST, res);
                return;
            }

            Long newId = AddressService.create(dto);

            successResponse(newId, "Address created", null, null, HttpServletResponse.SC_CREATED, res);
        } catch (Exception e) {
            errorResponse("Failed to process request", e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, res);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");
            if (req.getPathInfo() == null) {
                errorResponse("Not allowed", "Invalid parameter.", HttpServletResponse.SC_METHOD_NOT_ALLOWED, res);
                return;
            }
            Address address;

            try {
                Long id = Long.parseLong(req.getPathInfo().replaceFirst("/", ""));

                address = AddressService.get(id);
                if (address == null) {
                    errorResponse("Can't find address", "Address not found with the given parameter.", HttpServletResponse.SC_NOT_FOUND, res);
                }
            } catch (Exception ex) {
                errorResponse("Can't find address", "Invalid parameter.", HttpServletResponse.SC_BAD_REQUEST, res);
                return;
            }

            Gson gson = new Gson();
            AddressDTO dto = gson.fromJson(req.getReader(), AddressDTO.class);

            if (dto == null) {
                errorResponse("Can't update address", "No data submitted.", HttpServletResponse.SC_BAD_REQUEST, res);
                return;
            }
            if (!AddressService.validate(dto)) {
                errorResponse("Validation error", "Required fields not informed.", HttpServletResponse.SC_BAD_REQUEST, res);
                return;
            }

            Long id = AddressService.update(address, dto);

            successResponse(id, "Address updated", null, null, HttpServletResponse.SC_OK, res);
        } catch (Exception e) {
            errorResponse("Failed to process request", e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, res);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            if (req.getPathInfo() == null) {
                errorResponse("Not allowed", "Invalid parameter.", HttpServletResponse.SC_METHOD_NOT_ALLOWED, res);
                return;
            }
            Long id;
            Address address;
            try {
                id = Long.parseLong(req.getPathInfo().replaceFirst("/", ""));

                address = AddressService.get(id);
                if (address == null) {
                    errorResponse("Can't find address", "Address not found with the given parameter.", HttpServletResponse.SC_NOT_FOUND, res);
                }
            } catch (Exception ex) {
                errorResponse("Can't find address", "Invalid parameter.", HttpServletResponse.SC_BAD_REQUEST, res);
                return;
            }

            AddressService.delete(address);

            successResponse(id, "Address deleted", null, null, HttpServletResponse.SC_OK, res);
        } catch (Exception e) {
            errorResponse("Failed to process request", e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, res);
            throw new RuntimeException(e);
        }
    }
}
